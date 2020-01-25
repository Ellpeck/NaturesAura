package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class Shovel extends ShovelItem implements IModItem, IModelProvider {
    private final String baseName;

    public Shovel(String baseName, IItemTier material, float damage, float speed) {
        super(material, damage, speed, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (this == ModItems.INFUSED_IRON_SHOVEL) {
            PlayerEntity player = context.getPlayer();
            World world = context.getWorld();
            BlockPos pos = context.getPos();
            ItemStack stack = player.getHeldItem(context.getHand());
            BlockState state = world.getBlockState(pos);
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
                                    && world.getBlockState(actualPos).getBlock() == Blocks.GRASS) {
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
        if (this == ModItems.INFUSED_IRON_SHOVEL)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }
}
