package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemShovel extends ShovelItem implements IModItem, ICustomItemModel {
    private final String baseName;

    public ItemShovel(String baseName, IItemTier material, float damage, float speed) {
        super(material, damage, speed, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = player.getHeldItem(context.getHand());
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        if (this == ModItems.INFUSED_IRON_SHOVEL) {
            int damage = 0;
            if (state.getBlock() == Blocks.DIRT) {
                if (world.getBlockState(pos.up()).getMaterial() == Material.AIR) {
                    world.setBlockState(pos, Blocks.GRASS.getDefaultState());
                    damage = 5;
                }
            } else {
                int range = player.isSneaking() ? 0 : 1;
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        BlockPos actualPos = pos.add(x, 0, y);
                        Direction facing = context.getFace();
                        if (player.canPlayerEdit(actualPos.offset(facing), facing, stack)) {
                            if (facing != Direction.DOWN
                                    && world.getBlockState(actualPos.up()).getMaterial() == Material.AIR
                                    && world.getBlockState(actualPos).getBlock() == Blocks.GRASS_BLOCK) {
                                if (!world.isRemote) {
                                    world.setBlockState(actualPos, Blocks.GRASS_PATH.getDefaultState(), 11);
                                }
                                damage = 1;
                            }
                        }
                    }
                }
            }

            if (damage > 0) {
                world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.damageItem(damage, player, playerEntity -> playerEntity.sendBreakAnimation(context.getHand()));
                return ActionResultType.SUCCESS;
            }
        } else if (this == ModItems.SKY_SHOVEL) {
            if (this.getDestroySpeed(stack, state) <= 1)
                return super.onItemUse(context);
            Hand otherHand = context.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack other = player.getHeldItem(otherHand);
            if (other.isEmpty() || !(other.getItem() instanceof BlockItem))
                return super.onItemUse(context);
            world.removeBlock(pos, false);
            TileEntity tile = state.hasTileEntity() ? world.getTileEntity(pos) : null;
            Block.spawnDrops(state, world, pos, tile, null, ItemStack.EMPTY);
            ItemUseContext newContext = new ItemUseContext(player, otherHand, new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), context.isInside()));
            other.onItemUse(newContext);
            stack.damageItem(1, player, p -> p.sendBreakAnimation(context.getHand()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return Helper.makeRechargeProvider(stack, true);
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
