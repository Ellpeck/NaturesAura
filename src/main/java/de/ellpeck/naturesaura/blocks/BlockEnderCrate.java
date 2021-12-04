package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEnderCrate;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderEnderCrate;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.tileentity.BlockEntityType;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockEnderCrate extends BlockContainerImpl implements ITESRProvider<BlockEntityEnderCrate>, ICustomBlockState {

    // This is terrible but I can't see a better solution right now so oh well
    private static final ThreadLocal<WeakReference<Level>> CACHED_WORLD = new ThreadLocal<>();

    public BlockEnderCrate() {
        super("ender_crate", BlockEntityEnderCrate::new, Properties.create(Material.ROCK).hardnessAndResistance(5F).setLightLevel(s -> 7).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static String getEnderName(ItemStack stack) {
        if (!stack.hasTag())
            return "";
        return stack.getTag().getString(NaturesAura.MOD_ID + ":ender_name");
    }

    @OnlyIn(Dist.CLIENT)
    public static void addEnderNameInfo(ItemStack stack, List<ITextComponent> tooltip) {
        String name = getEnderName(stack);
        if (name != null && !name.isEmpty())
            tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + I18n.format("info." + NaturesAura.MOD_ID + ".ender_name",
                    TextFormatting.ITALIC + name + TextFormatting.RESET)));
        else
            tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + I18n.format("info." + NaturesAura.MOD_ID + ".ender_name.missing")));
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof AnvilBlock) {
            CACHED_WORLD.set(new WeakReference<>(level));
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        WeakReference<Level> level = CACHED_WORLD.get();
        if (level == null || level.get() == null)
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
        if (ILevelData.getOverworldData(level.get()).isEnderStorageLocked(name))
            return;
        ItemStack output = stack.copy();
        output.getOrCreateTag().putString(NaturesAura.MOD_ID + ":ender_name", name);
        event.setOutput(output);
        event.setMaterialCost(stack.getCount());
        event.setCost(1);
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Level levelIn, BlockPos pos, Player player, Hand handIn, BlockRayTraceResult hit) {
        if (!levelIn.isClientSide) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityEnderCrate) {
                BlockEntityEnderCrate crate = (BlockEntityEnderCrate) tile;
                if (crate.canOpen()) {
                    crate.drainAura(2500);
                    NetworkHooks.openGui((ServerPlayer) player, crate, pos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader levelIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addEnderNameInfo(stack, tooltip);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; ++i) {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
            double d1 = (float) pos.getY() + rand.nextFloat();
            double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
            double d3 = rand.nextFloat() * (float) j;
            double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = rand.nextFloat() * (float) k;
            levelIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public Tuple<BlockEntityType<BlockEntityEnderCrate>, Supplier<Function<? super BlockEntityRendererDispatcher, ? extends BlockEntityRenderer<? super BlockEntityEnderCrate>>>> getTESR() {
        return new Tuple<>(ModTileEntities.ENDER_CRATE, () -> RenderEnderCrate::new);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
