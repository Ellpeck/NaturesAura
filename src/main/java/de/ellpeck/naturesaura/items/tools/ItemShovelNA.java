package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

public class ItemShovelNA extends ShovelItem implements IModItem, ICreativeItem, IModelProvider {
    private final String baseName;

    public ItemShovelNA(String baseName, ToolMaterial material) {
        super(material);
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (this == ModItems.INFUSED_SHOVEL) {
            ItemStack stack = player.getHeldItem(hand);
            BlockState state = worldIn.getBlockState(pos);
            int damage = 0;

            if (state.getBlock() instanceof BlockDirt) {
                if (worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR) {
                    worldIn.setBlockState(pos, Blocks.GRASS.getDefaultState());
                    damage = 5;
                }
            } else {
                int range = player.isSneaking() ? 0 : 1;
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        BlockPos actualPos = pos.add(x, 0, y);
                        if (player.canPlayerEdit(actualPos.offset(facing), facing, stack)) {
                            if (facing != Direction.DOWN
                                    && worldIn.getBlockState(actualPos.up()).getMaterial() == Material.AIR
                                    && worldIn.getBlockState(actualPos).getBlock() == Blocks.GRASS) {
                                if (!worldIn.isRemote) {
                                    worldIn.setBlockState(actualPos, Blocks.GRASS_PATH.getDefaultState(), 11);
                                }
                                damage = 1;
                            }
                        }
                    }
                }
            }

            if (damage > 0) {
                worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.damageItem(damage, player);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (this == ModItems.INFUSED_SHOVEL)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }
}
