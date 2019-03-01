package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler.FancyInfo;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.PatchouliAPI;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

public final class PatchouliCompat {

    private static final ResourceLocation BOOK = new ResourceLocation(NaturesAura.MOD_ID, "book");

    public static void preInit() {
        PatchouliAPI.instance.setConfigFlag(NaturesAura.MOD_ID + ":rf_converter", ModConfig.enabledFeatures.rfConverter);
    }

    @SideOnly(Side.CLIENT)
    public static void onGuiRender(GuiScreen gui, int mouseX, int mouseY) {
        boolean display = false;
        try {
            ResourceLocation open = PatchouliAPI.instance.getOpenBookGui();
            display = open != null && open.equals(BOOK);
        } catch (Throwable ignored) {
            // TODO remove this once Patchouli update is out long enough
        }
        if (display) {
            LocalDateTime now = LocalDateTime.now();
            if (now.getMonth() == Month.MAY && now.getDayOfMonth() == 21) {
                int x = gui.width / 2 + 272 / 2 - 16;
                int y = gui.height / 2 - 180 / 2 - 26;

                RenderHelper.disableStandardItemLighting();
                GlStateManager.color(1, 1, 1, 1);
                gui.mc.getTextureManager().bindTexture(ClientEvents.BOOK_GUI);
                Gui.drawModalRectWithCustomSizedTexture(x, y, 469, 0, 43, 42, 512, 256);

                if (mouseX >= x && mouseY >= y && mouseX < x + 43 && mouseY < y + 42)
                    GuiUtils.drawHoveringText(
                            Collections.singletonList(TextFormatting.GOLD + "It's the author Ellpeck's birthday!"),
                            mouseX, mouseY, gui.width, gui.height, 0, gui.mc.fontRenderer);
            }

            String name = gui.mc.player.getName();
            FancyInfo info = SupporterFancyHandler.FANCY_INFOS.get(name);
            if (info != null) {
                int x = gui.width / 2 - 272 / 2 + 20;
                int y = gui.height / 2 + 180 / 2;

                RenderHelper.disableStandardItemLighting();
                GlStateManager.color(1, 1, 1, 1);
                gui.mc.getTextureManager().bindTexture(ClientEvents.BOOK_GUI);

                Gui.drawModalRectWithCustomSizedTexture(x, y, 496, 44, 16, 18, 512, 256);
                if (info.tier == 1) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 496 - 16, 44, 16, 18, 512, 256);
                } else {
                    float r = ((info.color >> 16) & 255) / 255F;
                    float g = ((info.color >> 8) & 255) / 255F;
                    float b = (info.color & 255) / 255F;
                    GlStateManager.color(r, g, b);
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 496 - 32, 44, 16, 18, 512, 256);
                }

                if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 18)
                    GuiUtils.drawHoveringText(
                            Collections.singletonList(TextFormatting.YELLOW + "Thanks for your support, " + name + "!"),
                            mouseX, mouseY, gui.width, gui.height, 0, gui.mc.fontRenderer);

            }
        }
    }

    public static void addPatchouliMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        for (int i = 1; i < rawMatchers.length; i += 2) {
            if (rawMatchers[i] instanceof Matcher) {
                Matcher matcher = (Matcher) rawMatchers[i];
                Matcher.ICheck check = matcher.getCheck();
                if (check == null)
                    rawMatchers[i] = PatchouliAPI.instance.anyMatcher();
                else
                    rawMatchers[i] = PatchouliAPI.instance.predicateMatcher(matcher.getDefaultState(),
                            state -> check.matches(null, null, null, null, state, (char) 0));
            }
        }
        PatchouliAPI.instance.registerMultiblock(name, PatchouliAPI.instance.makeMultiblock(pattern, rawMatchers));
    }
}
