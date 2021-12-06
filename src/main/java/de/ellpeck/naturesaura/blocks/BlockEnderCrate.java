package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEnderCrate;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderEnderCrate;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockEnderCrate extends BlockContainerImpl implements ITESRProvider<BlockEntityEnderCrate>, ICustomBlockState {

    public BlockEnderCrate() {
        super("ender_crate", BlockEntityEnderCrate::new, Properties.of(Material.STONE).strength(5F).lightLevel(s -> 7).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static String getEnderName(ItemStack stack) {
        if (!stack.hasTag())
            return "";
        return stack.getTag().getString(NaturesAura.MOD_ID + ":ender_name");
    }

    @OnlyIn(Dist.CLIENT)
    public static void addEnderNameInfo(ItemStack stack, List<Component> tooltip) {
        String name = getEnderName(stack);
        if (name != null && !name.isEmpty()) {
            tooltip.add(new TextComponent(ChatFormatting.DARK_PURPLE + I18n.get("info." + NaturesAura.MOD_ID + ".ender_name", ChatFormatting.ITALIC + name + ChatFormatting.RESET)));
        } else {
            tooltip.add(new TextComponent(ChatFormatting.DARK_PURPLE + I18n.get("info." + NaturesAura.MOD_ID + ".ender_name.missing")));
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        var player = event.getPlayer();
        if (player == null)
            return;
        ItemStack stack = event.getLeft();
        if (stack.getItem() != this.asItem() && stack.getItem() != ModItems.ENDER_ACCESS)
            return;
        ItemStack second = event.getRight();
        if (second.getItem() != Items.ENDER_EYE || second.getCount() < stack.getCount())
            return;
        String name = event.getName();
        if (name == null || name.isEmpty())
            return;
        if (ILevelData.getOverworldData(player.level).isEnderStorageLocked(name))
            return;
        ItemStack output = stack.copy();
        output.getOrCreateTag().putString(NaturesAura.MOD_ID + ":ender_name", name);
        event.setOutput(output);
        event.setMaterialCost(stack.getCount());
        event.setCost(1);
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!levelIn.isClientSide) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityEnderCrate crate && crate.canOpen()) {
                crate.drainAura(2500);
                NetworkHooks.openGui((ServerPlayer) player, crate, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter levelIn, List<Component> tooltip, TooltipFlag flagIn) {
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
    public Tuple<BlockEntityType<BlockEntityEnderCrate>, Supplier<Function<? super BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<? super BlockEntityEnderCrate>>>> getTESR() {
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
