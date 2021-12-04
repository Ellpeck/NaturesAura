package de.ellpeck.naturesaura.blocks.tiles.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEnderCrate;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class RenderEnderCrate extends BlockEntityRenderer<BlockEntityEnderCrate> {
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj(i -> RenderType.getEndPortal(i + 1)).collect(ImmutableList.toImmutableList());

    public RenderEnderCrate(BlockEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(BlockEntityEnderCrate tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RANDOM.setSeed(31100L);
        double d0 = tileEntityIn.getPos().distanceSq(this.renderDispatcher.renderInfo.getProjectedView(), true);
        int i = this.getPasses(d0);
        float f = this.getOffset();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        this.renderCube(f, 0.15F, matrix4f, bufferIn.getBuffer(RENDER_TYPES.get(0)));

        for (int j = 1; j < i; ++j) {
            this.renderCube(f, 2.0F / (float) (18 - j), matrix4f, bufferIn.getBuffer(RENDER_TYPES.get(j)));
        }
    }

    private void renderCube(float g, float h, Matrix4f mat, IVertexBuilder builder) {
        float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * h;
        float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * h;
        float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * h;
        this.renderFace(mat, builder, g, g, f, f1, f2);
    }

    private void renderFace(Matrix4f mat, IVertexBuilder builder, float h, float i, float n, float o, float p) {
        builder.pos(mat, 2 / 16F, h, 14 / 16F).color(n, o, p, 1.0F).endVertex();
        builder.pos(mat, 14 / 16F, h, 14 / 16F).color(n, o, p, 1.0F).endVertex();
        builder.pos(mat, 14 / 16F, i, 2 / 16F).color(n, o, p, 1.0F).endVertex();
        builder.pos(mat, 2 / 16F, i, 2 / 16F).color(n, o, p, 1.0F).endVertex();
    }

    protected int getPasses(double dist) {
        int i;

        if (dist > 36864.0D) {
            i = 1;
        } else if (dist > 25600.0D) {
            i = 3;
        } else if (dist > 16384.0D) {
            i = 5;
        } else if (dist > 9216.0D) {
            i = 7;
        } else if (dist > 4096.0D) {
            i = 9;
        } else if (dist > 1024.0D) {
            i = 11;
        } else if (dist > 576.0D) {
            i = 13;
        } else if (dist > 256.0D) {
            i = 14;
        } else {
            i = 15;
        }

        return i;
    }

    protected float getOffset() {
        return 1.001F;
    }
}
