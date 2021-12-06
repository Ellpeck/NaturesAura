package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.misc.ColoredBlockHelper;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemUseContext;
import net.minecraft.level.Level;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemColorChanger extends ItemImpl implements IColorProvidingItem, ICustomItemModel {

    public ItemColorChanger() {
        super("color_changer", new Properties().maxStackSize(1));
    }

    private static boolean changeOrCopyColor(Player player, ItemStack stack, Level level, BlockPos pos, DyeColor firstColor) {
        Block block = level.getBlockState(pos).getBlock();
        List<Block> blocks = ColoredBlockHelper.getBlocksContaining(block);
        if (blocks == null)
            return false;
        DyeColor color = DyeColor.byId(blocks.indexOf(block));
        if (firstColor == null || color == firstColor) {
            DyeColor stored = getStoredColor(stack);
            if (player.isSneaking()) {
                if (stored != color) {
                    level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.65F, 1F);
                    if (!level.isClientSide)
                        storeColor(stack, color);
                    return true;
                }
            } else {
                if (stored != null && stored != color) {
                    if (NaturesAuraAPI.instance().extractAuraFromPlayer(player, 1000, level.isClientSide)) {
                        if (firstColor == null) {
                            level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 0.65F, 1F);
                        }
                        if (!level.isClientSide) {
                            level.setBlockState(pos, blocks.get(stored.getId()).getDefaultState());

                            if (isFillMode(stack)) {
                                for (Direction off : Direction.values()) {
                                    changeOrCopyColor(player, stack, level, pos.offset(off), color);
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
            int color = stack.getTag().getInt("color");
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
    public InteractionResult onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        if (changeOrCopyColor(context.getPlayer(), stack, context.getLevel(), context.getPos(), null)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking() && getStoredColor(stack) != null) {
            levelIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.PLAYERS, 0.65F, 1F);
            if (!levelIn.isClientSide) {
                setFillMode(stack, !isFillMode(stack));
            }
            return new ActionResult<>(InteractionResult.SUCCESS, stack);
        } else {
            return new ActionResult<>(InteractionResult.PASS, stack);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> {
            if (tintIndex > 0) {
                DyeColor color = getStoredColor(stack);
                if (color != null) {
                    return color.getColorValue();
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
