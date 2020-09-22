package de.ellpeck.naturesaura.entities.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class RenderMoverMinecart extends MinecartRenderer<EntityMoverMinecart> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/mover_cart.png");
    private final ModelMoverMinecart model = new ModelMoverMinecart();

    public RenderMoverMinecart(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(EntityMoverMinecart entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void renderBlockState(EntityMoverMinecart entityIn, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0, 22 / 16F, 0);
        matrixStackIn.translate(0, 0, 1);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
        this.model.render(matrixStackIn, bufferIn.getBuffer(this.model.getRenderType(RES)), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStackIn.pop();
    }

    private static class ModelMoverMinecart extends Model {

        private final ModelRenderer box;

        public ModelMoverMinecart() {
            super(RenderType::getEntityCutout);
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 24, 16);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
