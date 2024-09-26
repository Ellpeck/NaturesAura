package de.ellpeck.naturesaura.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEnderCrate;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderEnderCrate;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import java.util.List;

public class BlockEnderCrate extends BlockContainerImpl implements ITESRProvider<BlockEntityEnderCrate>, ICustomBlockState {

    public BlockEnderCrate() {
        super("ender_crate", BlockEntityEnderCrate.class, Properties.of().strength(5F).lightLevel(s -> 7).sound(SoundType.STONE));

        NeoForge.EVENT_BUS.register(this);
    }

    public static String getEnderName(ItemStack stack) {
        if (!stack.has(Data.TYPE))
            return "";
        return stack.get(Data.TYPE).enderName;
    }

    @OnlyIn(Dist.CLIENT)
    public static void addEnderNameInfo(ItemStack stack, List<Component> tooltip) {
        var name = BlockEnderCrate.getEnderName(stack);
        if (name != null && !name.isEmpty()) {
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("info." + NaturesAura.MOD_ID + ".ender_name", ChatFormatting.ITALIC + name + ChatFormatting.RESET)));
        } else {
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("info." + NaturesAura.MOD_ID + ".ender_name.missing")));
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        var player = event.getPlayer();
        if (player == null)
            return;
        var stack = event.getLeft();
        if (stack.getItem() != this.asItem() && stack.getItem() != ModItems.ENDER_ACCESS)
            return;
        var second = event.getRight();
        if (second.getItem() != Items.ENDER_EYE || second.getCount() < stack.getCount())
            return;
        var name = event.getName();
        if (name == null || name.isEmpty())
            return;
        if (ILevelData.getOverworldData(player.level()).isEnderStorageLocked(name))
            return;
        var output = stack.copy();
        output.set(Data.TYPE, new Data(name));
        event.setOutput(output);
        event.setMaterialCost(stack.getCount());
        event.setCost(1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            var tile = level.getBlockEntity(pos);
            if (tile instanceof BlockEntityEnderCrate crate && crate.canOpen() && crate.canUseRightNow(2500)) {
                crate.drainAura(2500);
                player.openMenu(crate, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BlockEnderCrate.addEnderNameInfo(stack, tooltipComponents);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, RandomSource rand) {
        for (var i = 0; i < 3; ++i) {
            var j = rand.nextInt(2) * 2 - 1;
            var k = rand.nextInt(2) * 2 - 1;
            var d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
            double d1 = (float) pos.getY() + rand.nextFloat();
            var d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
            double d3 = rand.nextFloat() * (float) j;
            var d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = rand.nextFloat() * (float) k;
            levelIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
            generator.modLoc("block/" + this.getBaseName()),
            generator.modLoc("block/" + this.getBaseName() + "_bottom"),
            generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    @Override
    public void registerTESR() {
        BlockEntityRenderers.register(ModBlockEntities.ENDER_CRATE, RenderEnderCrate::new);
    }

    public record Data(String enderName) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("name").forGetter(d -> d.enderName)
        ).apply(i, Data::new));
        public static final DataComponentType<Data> TYPE = DataComponentType.<Data>builder().persistent(Data.CODEC).cacheEncoding().build();

    }

}
