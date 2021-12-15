package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.misc.ColoredBlockHelper;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

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
            var stored = getStoredColor(stack);
            if (player.isCrouching()) {
                if (stored != color) {
                    level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 0.65F, 1F);
                    if (!level.isClientSide)
                        storeColor(stack, color);
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

                            if (isFillMode(stack)) {
                                for (var off : Direction.values()) {
                                    changeOrCopyColor(player, stack, level, pos.relative(off), color);
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
        if (!stack.hasTag()) {
            return null;
        } else {
            var color = stack.getTag().getInt("color");
            return DyeColor.byId(color);
        }
    }

    private static void storeColor(ItemStack stack, DyeColor color) {
        stack.getOrCreateTag().putInt("color", color.getId());
    }

    public static boolean isFillMode(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        } else {
            return stack.getTag().getBoolean("fill");
        }
    }

    private static void setFillMode(ItemStack stack, boolean fill) {
        stack.getOrCreateTag().putBoolean("fill", fill);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var stack = context.getPlayer().getItemInHand(context.getHand());
        if (changeOrCopyColor(context.getPlayer(), stack, context.getLevel(), context.getClickedPos(), null)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (playerIn.isCrouching() && getStoredColor(stack) != null) {
            levelIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.BUCKET_FILL_LAVA, SoundSource.PLAYERS, 0.65F, 1F);
            if (!levelIn.isClientSide) {
                setFillMode(stack, !isFillMode(stack));
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
                var color = getStoredColor(stack);
                if (color != null) {
                    return color.getFireworkColor();
                }
            }
            return 0xFFFFFF;
        };
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        // noop
    }
}
