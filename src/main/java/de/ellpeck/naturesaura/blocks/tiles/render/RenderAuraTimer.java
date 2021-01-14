package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraTimer;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderAuraTimer extends TileEntityRenderer<TileEntityAuraTimer> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/aura_timer_aura.png");
    private final AuraModel model = new AuraModel();

    public RenderAuraTimer(TileEntityRendererDispatcher disp) {
        super(disp);
    }

    @Override
    public void render(TileEntityAuraTimer tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        ItemStack bottle = tile.getItemHandler().getStackInSlot(0);
        if (bottle.isEmpty())
            return;
        stack.push();
        stack.translate(4 / 16F, 2.001F / 16, 4 / 16F);

        float percentage = 1 - tile.getTimerPercentage();
        stack.scale(8 / 16F, 6.5F / 16 * percentage, 8 / 16F);

        IAuraType type = ItemAuraBottle.getType(bottle);
        float r = (type.getColor() >> 16 & 255) / 255F;
        float g = (type.getColor() >> 8 & 255) / 255F;
        float b = (type.getColor() & 255) / 255F;
        this.model.render(stack, buffer.getBuffer(this.model.getRenderType(RES)), combinedLightIn, combinedOverlayIn, r, g, b, 0.75F);
        stack.pop();
    }

    private static class AuraModel extends Model {

        private final ModelRenderer box;

        public AuraModel() {
            super(RenderType::getEntityTranslucent);
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
