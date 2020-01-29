package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.misc.ColoredBlockHelper;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemColorChanger extends ItemImpl implements IColorProvidingItem, ICustomItemModel {

    public ItemColorChanger() {
        super("color_changer", new Properties().maxStackSize(1).group(NaturesAura.CREATIVE_TAB));

        this.addPropertyOverride(new ResourceLocation(NaturesAura.MOD_ID, "fill_mode"),
                (stack, worldIn, entityIn) -> isFillMode(stack) ? 1F : 0F);
        this.addPropertyOverride(new ResourceLocation(NaturesAura.MOD_ID, "has_color"),
                (stack, worldIn, entityIn) -> getStoredColor(stack) != null ? 1F : 0F);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        if (changeOrCopyColor(context.getPlayer(), stack, context.getWorld(), context.getPos(), null)) {
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    private static boolean changeOrCopyColor(PlayerEntity player, ItemStack stack, World world, BlockPos pos, DyeColor firstColor) {
        Block block = world.getBlockState(pos).getBlock();
        List<Block> blocks = ColoredBlockHelper.getBlocksContaining(block);
        if (blocks == null)
            return false;
        DyeColor color = DyeColor.byId(blocks.indexOf(block));
        if (firstColor == null || color == firstColor) {
            DyeColor stored = getStoredColor(stack);
            if (player.isShiftKeyDown()) {
                if (stored != color) {
                    world.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.65F, 1F);
                    if (!world.isRemote)
                        storeColor(stack, color);
                    return true;
                }
            } else {
                if (stored != null && stored != color) {
                    if (NaturesAuraAPI.instance().extractAuraFromPlayer(player, 1000, world.isRemote)) {
                        if (firstColor == null) {
                            world.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 0.65F, 1F);
                        }
                        if (!world.isRemote) {
                            world.setBlockState(pos, blocks.get(stored.getId()).getDefaultState());

                            if (isFillMode(stack)) {
                                for (Direction off : Direction.values()) {
                                    changeOrCopyColor(player, stack, world, pos.offset(off), color);
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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isShiftKeyDown() && getStoredColor(stack) != null) {
            worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.PLAYERS, 0.65F, 1F);
            if (!worldIn.isRemote) {
                setFillMode(stack, !isFillMode(stack));
            }
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
    }

    private static DyeColor getStoredColor(ItemStack stack) {
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

    private static boolean isFillMode(ItemStack stack) {
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
