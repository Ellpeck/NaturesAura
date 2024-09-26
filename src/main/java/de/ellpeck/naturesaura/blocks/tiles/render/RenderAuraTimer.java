package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraTimer;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.capabilities.Capabilities;

public class RenderAuraTimer implements BlockEntityRenderer<BlockEntityAuraTimer> {

    private static final ResourceLocation RES = ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "textures/models/aura_timer_aura.png");
    private final AuraModel model = new AuraModel();

    public RenderAuraTimer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockEntityAuraTimer tile, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        var bottle = tile.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, null).getStackInSlot(0);
        if (bottle.isEmpty())
            return;
        stack.pushPose();
        stack.translate(4 / 16F, 2.001F / 16, 4 / 16F);

        var percentage = 1 - tile.getTimerPercentage();
        stack.scale(8 / 16F, 6.5F / 16 * percentage, 8 / 16F);

        var type = ItemAuraBottle.getType(bottle);
        var r = (type.getColor() >> 16 & 255) / 255F;
        var g = (type.getColor() >> 8 & 255) / 255F;
        var b = (type.getColor() & 255) / 255F;
        this.model.renderToBuffer(stack, buffer.getBuffer(this.model.renderType(RenderAuraTimer.RES)), combinedLightIn, combinedOverlayIn, FastColor.ARGB32.colorFromFloat(0.75F, r, g, b));
        stack.popPose();

    }

    private static class AuraModel extends Model {

        private final ModelPart model;

        public AuraModel() {
            super(RenderType::entityTranslucent);
            var mesh = new MeshDefinition();
            var part = mesh.getRoot();
            part.addOrReplaceChild("main", new CubeListBuilder().addBox(0, 0, 0, 16, 16, 16), PartPose.ZERO);
            this.model = LayerDefinition.create(mesh, 64, 64).bakeRoot();
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
            this.model.render(poseStack, buffer, packedLight, packedOverlay, color);
        }

    }

}
