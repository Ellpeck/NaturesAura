package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

public class ItemHoeNA extends HoeItem implements IModItem, ICreativeItem, IModelProvider {

    private final String baseName;

    public ItemHoeNA(String baseName, ToolMaterial material) {
        super(material);
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        ActionResultType result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        if (!worldIn.isRemote && result == ActionResultType.SUCCESS && this == ModItems.INFUSED_HOE) {
            ItemStack seed = ItemStack.EMPTY;

            if (worldIn.rand.nextInt(5) == 0) {
                seed = ForgeHooks.getGrassSeed(worldIn.rand, 0);
            } else if (worldIn.rand.nextInt(10) == 0) {
                int rand = worldIn.rand.nextInt(3);
                if (rand == 0) {
                    seed = new ItemStack(Items.MELON_SEEDS);
                } else if (rand == 1) {
                    seed = new ItemStack(Items.PUMPKIN_SEEDS);
                } else if (rand == 2) {
                    seed = new ItemStack(Items.BEETROOT_SEEDS);
                }
            }

            if (!seed.isEmpty()) {
                ItemEntity item = new ItemEntity(worldIn, pos.getX() + worldIn.rand.nextFloat(), pos.getY() + 1F, pos.getZ() + worldIn.rand.nextFloat(), seed);
                worldIn.spawnEntity(item);
            }
        }
        return result;
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
        if (this == ModItems.INFUSED_HOE)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }
}
