package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAuraBottle extends ItemImpl implements IColorProvidingItem {

    public ItemAuraBottle() {
        super("aura_bottle");
        MinecraftForge.EVENT_BUS.register(this);

        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.BOTTLE_TWO, new BehaviorDefaultDispenseItem() {
            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                World world = source.getWorld();
                IBlockState state = source.getBlockState();
                EnumFacing facing = state.getValue(BlockDispenser.FACING);
                BlockPos offset = source.getBlockPos().offset(facing);
                IBlockState offsetState = world.getBlockState(offset);

                ItemStack dispense = stack.splitStack(1);
                if (offsetState.getBlock().isAir(offsetState, world, offset)) {
                    if (IAuraChunk.getAuraInArea(world, offset, 30) >= 100000) {
                        dispense = setType(new ItemStack(ItemAuraBottle.this), IAuraType.forWorld(world));

                        BlockPos spot = IAuraChunk.getHighestSpot(world, offset, 30, offset);
                        IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 20000);
                    }
                }

                doDispense(world, dispense, 6, facing, BlockDispenser.getDispensePosition(source));
                return stack;
            }
        });
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || held.getItem() != ModItems.BOTTLE_TWO)
            return;
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult ray = this.rayTrace(player.world, player, true);
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK)
            return;
        BlockPos pos = player.getPosition();
        if (IAuraChunk.getAuraInArea(player.world, pos, 30) < 100000)
            return;

        if (!player.world.isRemote) {
            held.shrink(1);

            player.inventory.addItemStackToInventory(
                    setType(new ItemStack(this), IAuraType.forWorld(player.world)));

            BlockPos spot = IAuraChunk.getHighestSpot(player.world, pos, 30, pos);
            IAuraChunk.getAuraChunk(player.world, spot).drainAura(spot, 20000);

            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.PLAYERS, 1F, 1F);
        }
        player.swingArm(event.getHand());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (IAuraType type : NaturesAuraAPI.AURA_TYPES.values()) {
                ItemStack stack = new ItemStack(this);
                setType(stack, type);
                items.add(stack);
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + "." + getType(stack).getName() + ".name").trim();
    }

    public static IAuraType getType(ItemStack stack) {
        if (!stack.hasTagCompound())
            return NaturesAuraAPI.TYPE_OTHER;
        String type = stack.getTagCompound().getString("stored_type");
        if (type.isEmpty())
            return NaturesAuraAPI.TYPE_OTHER;
        return NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(type));
    }

    public static ItemStack setType(ItemStack stack, IAuraType type) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("stored_type", type.getName().toString());
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> tintIndex > 0 ? getType(stack).getColor() : 0xFFFFFF;
    }
}
