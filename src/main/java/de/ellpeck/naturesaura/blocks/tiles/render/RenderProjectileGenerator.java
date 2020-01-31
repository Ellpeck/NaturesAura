package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileGenerator extends TileEntityRenderer<TileEntityProjectileGenerator> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/projectile_generator_overlay.png");
    private final ModelOverlay model = new ModelOverlay();

    public RenderProjectileGenerator(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityProjectileGenerator te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        stack.push();
        if (te.nextSide == Direction.NORTH) {
            stack.rotate(Vector3f.YP.rotationDegrees(270));
            stack.translate(-0.002F, 0, -1);
        } else if (te.nextSide == Direction.EAST) {
            stack.rotate(Vector3f.YP.rotationDegrees(180));
            stack.translate(-1.002F, 0, -1);
        } else if (te.nextSide == Direction.SOUTH) {
            stack.rotate(Vector3f.YP.rotationDegrees(90));
            stack.translate(-1.002F, 0, 0);
        } else {
            stack.translate(-0.002F, 0, 0);
        }
        int brightness = 15 << 20 | 15 << 4;
        this.model.render(stack, buffer.getBuffer(this.model.getRenderType(RES)), brightness, combinedOverlayIn, 1, 1, 1, 1);
        stack.pop();
    }

    private static class ModelOverlay extends Model {

        private final ModelRenderer box;

        public ModelOverlay() {
            super(RenderType::entityTranslucent);
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
