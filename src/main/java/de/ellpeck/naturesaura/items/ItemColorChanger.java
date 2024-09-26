package de.ellpeck.naturesaura.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.misc.ColoredBlockHelper;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemColorChanger extends ItemImpl implements IColorProvidingItem, ICustomItemModel {

    public ItemColorChanger() {
        super("color_changer", new Properties().stacksTo(1));
    }

    private static boolean changeOrCopyColor(Player player, ItemStack stack, Level level, BlockPos pos, DyeColor firstColor) {
        var block = level.getBlockState(pos).getBlock();
        var blocks = ColoredBlockHelper.getBlocksContaining(block);
        if (blocks == null)
            return false;
        var color = DyeColor.byId(blocks.indexOf(block));
        if (firstColor == null || color == firstColor) {
            var stored = ItemColorChanger.getStoredColor(stack);
            if (player.isShiftKeyDown()) {
                if (stored != color) {
                    level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 0.65F, 1F);
                    if (!level.isClientSide)
                        ItemColorChanger.storeColor(stack, color);
                    return true;
                }
            } else {
                if (stored != null && stored != color) {
                    if (NaturesAuraAPI.instance().extractAuraFromPlayer(player, 1000, level.isClientSide)) {
                        if (firstColor == null) {
                            level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 0.65F, 1F);
                        }
                        if (!level.isClientSide) {
                            level.setBlockAndUpdate(pos, blocks.get(stored.getId()).defaultBlockState());

                            if (ItemColorChanger.isFillMode(stack)) {
                                for (var off : Direction.values()) {
                                    ItemColorChanger.changeOrCopyColor(player, stack, level, pos.relative(off), color);
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static DyeColor getStoredColor(ItemStack stack) {
        return stack.has(Data.TYPE) ? DyeColor.byId(stack.get(Data.TYPE).color) : null;
    }

    private static void storeColor(ItemStack stack, DyeColor color) {
        var previous = stack.getOrDefault(Data.TYPE, new Data(0, false));
        stack.set(Data.TYPE, new Data(color.getId(), previous.fill));
    }

    public static boolean isFillMode(ItemStack stack) {
        return stack.has(Data.TYPE) && stack.get(Data.TYPE).fill;
    }

    private static void setFillMode(ItemStack stack, boolean fill) {
        var previous = stack.getOrDefault(Data.TYPE, new Data(0, false));
        stack.set(Data.TYPE, new Data(previous.color, fill));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var stack = context.getPlayer().getItemInHand(context.getHand());
        if (ItemColorChanger.changeOrCopyColor(context.getPlayer(), stack, context.getLevel(), context.getClickedPos(), null)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (playerIn.isShiftKeyDown() && ItemColorChanger.getStoredColor(stack) != null) {
            levelIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.BUCKET_FILL_LAVA, SoundSource.PLAYERS, 0.65F, 1F);
            if (!levelIn.isClientSide) {
                ItemColorChanger.setFillMode(stack, !ItemColorChanger.isFillMode(stack));
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> {
            if (tintIndex > 0) {
                var color = ItemColorChanger.getStoredColor(stack);
                if (color != null) {
                    return FastColor.ARGB32.opaque(color.getFireworkColor());
                }
            }
            return 0xFFFFFFFF;
        };
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        // noop
    }

    public record Data(int color, boolean fill) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("color").forGetter(d -> d.color),
            Codec.BOOL.fieldOf("fill").forGetter(d -> d.fill)
        ).apply(i, Data::new));
        public static final DataComponentType<Data> TYPE = DataComponentType.<Data>builder().persistent(Data.CODEC).cacheEncoding().build();

    }

}
