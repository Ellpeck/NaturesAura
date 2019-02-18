package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityEnderCrate;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderEnderCrate;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

public class BlockEnderCrate extends BlockContainerImpl implements ITESRProvider {

    // This is terrible but I can't see a better solution right now so oh well
    private static final ThreadLocal<WeakReference<World>> CACHED_WORLD = new ThreadLocal<>();

    public BlockEnderCrate() {
        super(Material.ROCK, "ender_crate", TileEntityEnderCrate.class, "ender_crate");
        this.setSoundType(SoundType.STONE);
        this.setHardness(5F);
        this.setLightLevel(0.75F);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static String getEnderName(ItemStack stack) {
        if (!stack.hasTagCompound())
            return "";
        NBTTagCompound compound = stack.getTagCompound();
        return compound.getString(NaturesAura.MOD_ID + ":ender_name");
    }

    @SideOnly(Side.CLIENT)
    public static void addEnderNameInfo(ItemStack stack, List<String> tooltip) {
        String name = getEnderName(stack);
        if (name != null && !name.isEmpty())
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info." + NaturesAura.MOD_ID + ".ender_name",
                    TextFormatting.ITALIC + name + TextFormatting.RESET));
        else
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info." + NaturesAura.MOD_ID + ".ender_name.missing"));
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockAnvil) {
            CACHED_WORLD.set(new WeakReference<>(world));
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        WeakReference<World> world = CACHED_WORLD.get();
        if (world == null || world.get() == null)
            return;
        ItemStack stack = event.getLeft();
        if (stack.getItem() != Item.getItemFromBlock(this) && stack.getItem() != ModItems.ENDER_ACCESS)
            return;
        ItemStack second = event.getRight();
        if (second.getItem() != Items.ENDER_EYE || second.getCount() < stack.getCount())
            return;
        String name = event.getName();
        if (name == null || name.isEmpty())
            return;
        if (IWorldData.getOverworldData(world.get()).isEnderStorageLocked(name))
            return;
        ItemStack output = stack.copy();
        if (!output.hasTagCompound())
            output.setTagCompound(new NBTTagCompound());
        output.getTagCompound().setString(NaturesAura.MOD_ID + ":ender_name", name);
        event.setOutput(output);
        event.setMaterialCost(stack.getCount());
        event.setCost(1);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityEnderCrate) {
                TileEntityEnderCrate crate = (TileEntityEnderCrate) tile;
                if (crate.canOpen()) {
                    crate.drainAura(10000);
                    playerIn.openGui(NaturesAura.MOD_ID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addEnderNameInfo(stack, tooltip);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; ++i) {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
            double d1 = (double) ((float) pos.getY() + rand.nextFloat());
            double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
            double d3 = (double) (rand.nextFloat() * (float) j);
            double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = (double) (rand.nextFloat() * (float) k);
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Class, TileEntitySpecialRenderer> getTESR() {
        return new Tuple<>(TileEntityEnderCrate.class, new RenderEnderCrate());
    }
}
