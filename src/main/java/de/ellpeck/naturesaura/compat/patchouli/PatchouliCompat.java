package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.ICompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

// TODO Patchouli
public class PatchouliCompat implements ICompat {

    private static final ResourceLocation BOOK = new ResourceLocation(NaturesAura.MOD_ID, "book");
/*
    private static final Map<ResourceLocation, IMultiblock> MULTIBLOCKS = new HashMap<>();
*/

    public static void addPatchouliMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
     /*   for (int i = 1; i < rawMatchers.length; i += 2) {
            if (rawMatchers[i] instanceof Matcher) {
                Matcher matcher = (Matcher) rawMatchers[i];
                Matcher.ICheck check = matcher.getCheck();
                if (check == null)
                    rawMatchers[i] = PatchouliAPI.get().anyMatcher();
                else
                    rawMatchers[i] = PatchouliAPI.get().predicateMatcher(matcher.getDefaultState(),
                            state -> check.matches(null, null, null, null, state, (char) 0));
            }
        }
        MULTIBLOCKS.put(name, PatchouliAPI.get().makeMultiblock(pattern, rawMatchers));*/
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> T getRecipe(String type, String name) {
        var manager = Minecraft.getInstance().level.getRecipeManager();
        var res = new ResourceLocation(name);
        var pre = new ResourceLocation(res.getNamespace(), type + "/" + res.getPath());
        return (T) manager.byKey(pre).orElse(null);
    }

/*    public static IVariable ingredientVariable(Ingredient ingredient) {
        return IVariable.wrapList(Arrays.stream(ingredient.getMatchingStacks())
                .map(IVariable::from).collect(Collectors.toList()));
    }*/

    @Override
    public void setup(FMLCommonSetupEvent event) {
      /*  event.enqueueWork(() -> {
            for (Map.Entry<ResourceLocation, IMultiblock> entry : MULTIBLOCKS.entrySet())
                PatchouliAPI.get().registerMultiblock(entry.getKey(), entry.getValue());

            PatchouliAPI.get().setConfigFlag(NaturesAura.MOD_ID + ":rf_converter", ModConfig.instance.rfConverter.get());
            PatchouliAPI.get().setConfigFlag(NaturesAura.MOD_ID + ":chunk_loader", ModConfig.instance.chunkLoader.get());
        });*/
    }

    @Override
    public void setupClient() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addItemTags(ItemTagProvider provider) {

    }

 /*   @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBookDraw(BookDrawScreenEvent event) {
        if (event.book == null || !event.book.equals(BOOK))
            return;
        LocalDateTime now = LocalDateTime.now();
        if (now.getMonth() == Month.MAY && now.getDayOfMonth() == 21) {
            int x = event.gui.width / 2 + 272 / 2 - 16;
            int y = event.gui.height / 2 - 180 / 2 - 26;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.color4f(1, 1, 1, 1);
            event.gui.getMinecraft().getTextureManager().bindTexture(ClientEvents.BOOK_GUI);
            AbstractGui.blit(event.matrixStack, x, y, 469, 0, 43, 42, 512, 256);

            if (event.mouseX >= x && event.mouseY >= y && event.mouseX < x + 43 && event.mouseY < y + 42)
                GuiUtils.drawHoveringText(event.matrixStack,
                        Collections.singletonList(new StringTextComponent("It's the author Ellpeck's birthday!").setStyle(Style.EMPTY.setFormatting(TextFormatting.GOLD))),
                        event.mouseX, event.mouseY, event.gui.width, event.gui.height, 0, event.gui.getMinecraft().fontRenderer);
        } else if (now.getMonth() == Month.JUNE) {
            int x = event.gui.width / 2 + 272 / 2;
            int y = event.gui.height / 2 - 180 / 2 + 16;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.color4f(1, 1, 1, 1);
            event.gui.getMinecraft().getTextureManager().bindTexture(ClientEvents.BOOK_GUI);
            AbstractGui.blit(event.matrixStack, x, y, 424, 0, 45, 26, 512, 256);

            if (event.mouseX >= x && event.mouseY >= y && event.mouseX < x + 45 && event.mouseY < y + 26)
                GuiUtils.drawHoveringText(event.matrixStack,
                        Collections.singletonList(new StringTextComponent("§6Happy §4P§6r§ei§2d§9e§5!")),
                        event.mouseX, event.mouseY, event.gui.width, event.gui.height, 0, event.gui.getMinecraft().fontRenderer);
        }

        String name = event.gui.getMinecraft().player.getName().getString();
        FancyInfo info = SupporterFancyHandler.FANCY_INFOS.get(name);
        if (info != null) {
            int x = event.gui.width / 2 - 272 / 2 + 20;
            int y = event.gui.height / 2 + 180 / 2;

            RenderHelper.disableStandardItemLighting();
            RenderSystem.color4f(1, 1, 1, 1);
            event.gui.getMinecraft().getTextureManager().bindTexture(ClientEvents.BOOK_GUI);

            AbstractGui.blit(event.matrixStack, x, y, 496, 44, 16, 18, 512, 256);
            if (info.tier == 1) {
                AbstractGui.blit(event.matrixStack, x, y, 496 - 16, 44, 16, 18, 512, 256);
            } else {
                float r = ((info.color >> 16) & 255) / 255F;
                float g = ((info.color >> 8) & 255) / 255F;
                float b = (info.color & 255) / 255F;
                RenderSystem.color3f(r, g, b);
                AbstractGui.blit(event.matrixStack, x, y, 496 - 32, 44, 16, 18, 512, 256);
            }

            if (event.mouseX >= x && event.mouseY >= y && event.mouseX < x + 16 && event.mouseY < y + 18)
                GuiUtils.drawHoveringText(event.matrixStack,
                        Collections.singletonList(new StringTextComponent("Thanks for your support, " + name + "!").setStyle(Style.EMPTY.setFormatting(TextFormatting.YELLOW))),
                        event.mouseX, event.mouseY, event.gui.width, event.gui.height, 0, event.gui.getMinecraft().fontRenderer);

        }
    }*/
}
