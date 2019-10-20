package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler.FancyInfo;
import net.minecraft.client.gui.AbstractGui;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.BookDrawScreenEvent;
import vazkii.patchouli.api.PatchouliAPI;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.Map;

public final class PatchouliCompat {

    private static final ResourceLocation BOOK = new ResourceLocation(NaturesAura.MOD_ID, "book");

    public static void preInit() {
        PatchouliAPI.instance.setConfigFlag(NaturesAura.MOD_ID + ":rf_converter", ModConfig.enabledFeatures.rfConverter);
        PatchouliAPI.instance.setConfigFlag(NaturesAura.MOD_ID + ":chunk_loader", ModConfig.enabledFeatures.chunkLoader);
    }

    public static void preInitClient() {
        MinecraftForge.EVENT_BUS.register(new PatchouliCompat());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBookDraw(BookDrawScreenEvent event) {
        if (event.book == null || !event.book.equals(BOOK))
            return;
        LocalDateTime now = LocalDateTime.now();
        if (now.getMonth() == Month.MAY && now.getDayOfMonth() == 21) {
            int x = event.gui.width / 2 + 272 / 2 - 16;
            int y = event.gui.height / 2 - 180 / 2 - 26;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.color(1, 1, 1, 1);
            event.gui.mc.getTextureManager().bindTexture(ClientEvents.BOOK_GUI);
            AbstractGui.drawModalRectWithCustomSizedTexture(x, y, 469, 0, 43, 42, 512, 256);

            if (event.mouseX >= x && event.mouseY >= y && event.mouseX < x + 43 && event.mouseY < y + 42)
                GuiUtils.drawHoveringText(
                        Collections.singletonList(TextFormatting.GOLD + "It's the author Ellpeck's birthday!"),
                        event.mouseX, event.mouseY, event.gui.width, event.gui.height, 0, event.gui.mc.fontRenderer);
        }

        String name = event.gui.mc.player.getName();
        FancyInfo info = SupporterFancyHandler.FANCY_INFOS.get(name);
        if (info != null) {
            int x = event.gui.width / 2 - 272 / 2 + 20;
            int y = event.gui.height / 2 + 180 / 2;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.color(1, 1, 1, 1);
            event.gui.mc.getTextureManager().bindTexture(ClientEvents.BOOK_GUI);

            AbstractGui.drawModalRectWithCustomSizedTexture(x, y, 496, 44, 16, 18, 512, 256);
            if (info.tier == 1) {
                AbstractGui.drawModalRectWithCustomSizedTexture(x, y, 496 - 16, 44, 16, 18, 512, 256);
            } else {
                float r = ((info.color >> 16) & 255) / 255F;
                float g = ((info.color >> 8) & 255) / 255F;
                float b = (info.color & 255) / 255F;
                GlStateManager.color(r, g, b);
                AbstractGui.drawModalRectWithCustomSizedTexture(x, y, 496 - 32, 44, 16, 18, 512, 256);
            }

            if (event.mouseX >= x && event.mouseY >= y && event.mouseX < x + 16 && event.mouseY < y + 18)
                GuiUtils.drawHoveringText(
                        Collections.singletonList(TextFormatting.YELLOW + "Thanks for your support, " + name + "!"),
                        event.mouseX, event.mouseY, event.gui.width, event.gui.height, 0, event.gui.mc.fontRenderer);

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

    public static <T> T getRecipe(Map<ResourceLocation, T> recipes, String name) {
        ResourceLocation res = new ResourceLocation(name);
        T recipe = recipes.get(res);
        if (recipe == null)
            recipe = recipes.get(new ResourceLocation(Compat.CRAFT_TWEAKER, res.getPath()));
        return recipe;
    }
}
