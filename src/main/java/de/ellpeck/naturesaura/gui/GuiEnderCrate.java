package de.ellpeck.naturesaura.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEnderCrate extends AbstractContainerScreen<ContainerEnderCrate> {

    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public GuiEnderCrate(ContainerEnderCrate container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageHeight = 114 + 3 * 18;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);
        this.font.draw(matrixStack, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CHEST_GUI_TEXTURE);
        var i = (this.width - this.imageWidth) / 2;
        var j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, 3 * 18 + 17);
        this.blit(matrixStack, i, j + 3 * 18 + 17, 0, 126, this.imageHeight, 96);
    }
}