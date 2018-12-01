package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

public class ItemHoeNA extends ItemHoe implements IModItem, ICreativeItem, IModelProvider {

    private final String baseName;

    public ItemHoeNA(String baseName, ToolMaterial material) {
        super(material);
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        if (!worldIn.isRemote && result == EnumActionResult.SUCCESS && this == ModItems.INFUSED_HOE) {
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
                EntityItem item = new EntityItem(worldIn, pos.getX() + worldIn.rand.nextFloat(), pos.getY() + 1F, pos.getZ() + worldIn.rand.nextFloat(), seed);
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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (this == ModItems.INFUSED_HOE)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }
}
