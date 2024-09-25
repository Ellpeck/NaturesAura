package de.ellpeck.naturesaura.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEnderCrate extends AbstractContainerScreen<ContainerEnderCrate> {

    private static final ResourceLocation CHEST_GUI_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public GuiEnderCrate(ContainerEnderCrate container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageHeight = 114 + 3 * 18;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title.getString(), 8, 6, 4210752);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        var i = (this.width - this.imageWidth) / 2;
        var j = (this.height - this.imageHeight) / 2;
        graphics.blit(GuiEnderCrate.CHEST_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, 3 * 18 + 17);
        graphics.blit(GuiEnderCrate.CHEST_GUI_TEXTURE, i, j + 3 * 18 + 17, 0, 126, this.imageWidth, 96);
    }

}
