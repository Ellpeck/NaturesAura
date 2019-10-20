package de.ellpeck.naturesaura.gui;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

@OnlyIn(Dist.CLIENT)
public class GuiEnderCrate extends ContainerScreen {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final PlayerEntity player;
    private final String nameKey;
    private final String name;

    public GuiEnderCrate(PlayerEntity player, IItemHandler handler, String nameKey, String name) {
        super(new ContainerEnderCrate(player, handler));
        this.player = player;
        this.nameKey = nameKey;
        this.name = name;
        this.allowUserInput = false;
        this.ySize = 114 + 3 * 18;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String display = I18n.format("info." + NaturesAura.MOD_ID + "." + this.nameKey, TextFormatting.ITALIC + this.name + TextFormatting.RESET);
        this.fontRenderer.drawString(display, 8, 6, 4210752);
        this.fontRenderer.drawString(this.player.inventory.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 3 * 18 + 17);
        this.drawTexturedModalRect(i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
    }
}