package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityLowerLimiter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static de.ellpeck.naturesaura.blocks.tiles.render.RenderGeneratorLimitRemover.ModelLimitRemoverGlint;

@OnlyIn(Dist.CLIENT)
public class RenderLowerLimiter implements BlockEntityRenderer<BlockEntityLowerLimiter> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/lower_limiter_glint.png");
    private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    public RenderLowerLimiter(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(BlockEntityLowerLimiter te, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        for (var dir : Direction.values()) {
            var entity = te.getLevel().getBlockEntity(te.getBlockPos().relative(dir));
            if (entity instanceof BlockEntityImpl impl && impl.allowsLowerLimiter()) {
                RenderGeneratorLimitRemover.renderGlint(matrixStack, iRenderTypeBuffer, this.model, dir.getStepX(), dir.getStepY(), dir.getStepZ(), combinedOverlayIn, RenderLowerLimiter.RES);
                RenderGeneratorLimitRemover.renderGlint(matrixStack, iRenderTypeBuffer, this.model, 0, 0, 0, combinedOverlayIn, RenderLowerLimiter.RES);
            }
        }
    }

}
