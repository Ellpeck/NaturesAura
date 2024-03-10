package de.ellpeck.naturesaura.compat.patchouli;

import com.mojang.blaze3d.systems.RenderSystem;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.compat.ICompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import vazkii.patchouli.api.BookDrawScreenEvent;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.PatchouliAPI;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class PatchouliCompat implements ICompat {

    public static final ResourceLocation BOOK = new ResourceLocation(NaturesAura.MOD_ID, "book");
    private static final Map<ResourceLocation, IMultiblock> MULTIBLOCKS = new HashMap<>();

    public static void addPatchouliMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        for (var i = 1; i < rawMatchers.length; i += 2) {
            if (rawMatchers[i] instanceof Matcher matcher) {
                var check = matcher.check();
                if (check == null)
                    rawMatchers[i] = PatchouliAPI.get().anyMatcher();
                else
                    rawMatchers[i] = PatchouliAPI.get().predicateMatcher(matcher.defaultState(),
                            state -> check.matches(null, null, null, null, state, (char) 0));
            }
        }
        PatchouliCompat.MULTIBLOCKS.put(name, PatchouliAPI.get().makeMultiblock(pattern, rawMatchers));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> T getRecipe(String type, String name) {
        var manager = Minecraft.getInstance().level.getRecipeManager();
        var res = new ResourceLocation(name);
        var pre = new ResourceLocation(res.getNamespace(), type + "/" + res.getPath());
        return (T) manager.byKey(pre).orElse(null).value();
    }

    public static IVariable ingredientVariable(Ingredient ingredient) {
        return IVariable.wrapList(Arrays.stream(ingredient.getItems())
                .map(IVariable::from).collect(Collectors.toList()));
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (var entry : PatchouliCompat.MULTIBLOCKS.entrySet())
                PatchouliAPI.get().registerMultiblock(entry.getKey(), entry.getValue());

            PatchouliAPI.get().setConfigFlag(NaturesAura.MOD_ID + ":rf_converter", ModConfig.instance.rfConverter.get());
            PatchouliAPI.get().setConfigFlag(NaturesAura.MOD_ID + ":chunk_loader", ModConfig.instance.chunkLoader.get());
        });
    }

    @Override
    public void setupClient() {
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void addItemTags(ItemTagProvider provider) {

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBookDraw(BookDrawScreenEvent event) {
        var book = event.getBook();
        var gui = event.getScreen();
        if (book == null || !book.equals(PatchouliCompat.BOOK))
            return;
        var now = LocalDateTime.now();
        if (now.getMonth() == Month.MAY && now.getDayOfMonth() == 21) {
            var x = gui.width / 2 + 272 / 2 - 16;
            var y = gui.height / 2 - 180 / 2 - 26;

            event.getGraphics().blit(ClientEvents.BOOK_GUI, x, y, 469, 0, 43, 42, 512, 256);

            if (event.getMouseX() >= x && event.getMouseY() >= y && event.getMouseX() < x + 43 && event.getMouseY() < y + 42)
                event.getGraphics().renderTooltip(Minecraft.getInstance().font,
                        Collections.singletonList(Component.literal("It's the author Ellpeck's birthday!").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD))),
                        Optional.empty(),
                        event.getMouseX(), event.getMouseY());
        } else if (now.getMonth() == Month.JUNE) {
            var x = gui.width / 2 + 272 / 2;
            var y = gui.height / 2 + 32;

            event.getGraphics().blit(ClientEvents.BOOK_GUI, x, y, 424, 0, 45, 26, 512, 256);

            if (event.getMouseX() >= x && event.getMouseY() >= y && event.getMouseX() < x + 45 && event.getMouseY() < y + 26)
                //noinspection UnnecessaryUnicodeEscape
                event.getGraphics().renderTooltip(gui.getMinecraft().font,
                        Collections.singletonList(Component.literal("\u00A76Happy \u00A74P\u00A76r\u00A7ei\u00A72d\u00A79e\u00A75!")), Optional.empty(),
                        event.getMouseX(), event.getMouseY());
        }

        var name = gui.getMinecraft().player.getName().getString();
        var info = SupporterFancyHandler.FANCY_INFOS.get(name);
        if (info != null) {
            var x = gui.width / 2 - 272 / 2 + 20;
            var y = gui.height / 2 + 180 / 2;

            event.getGraphics().blit(ClientEvents.BOOK_GUI, x, y, 496, 44, 16, 18, 512, 256);
            if (info.tier() == 1) {
                event.getGraphics().blit(ClientEvents.BOOK_GUI, x, y, 496 - 16, 44, 16, 18, 512, 256);
            } else {
                var r = (info.color() >> 16 & 255) / 255F;
                var g = (info.color() >> 8 & 255) / 255F;
                var b = (info.color() & 255) / 255F;
                RenderSystem.setShaderColor(r, g, b, 1);
                event.getGraphics().blit(ClientEvents.BOOK_GUI, x, y, 496 - 32, 44, 16, 18, 512, 256);
            }

            if (event.getMouseX() >= x && event.getMouseY() >= y && event.getMouseX() < x + 16 && event.getMouseY() < y + 18)
                event.getGraphics().renderTooltip(gui.getMinecraft().font,
                        Collections.singletonList(Component.literal("Thanks for your support, " + name + "!").setStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW))), Optional.empty(),
                        event.getMouseX(), event.getMouseY());

        }
    }
}
