package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ItemAuraCache;
import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    public static final ResourceLocation OVERLAYS = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/overlays.png");
    public static final ResourceLocation BOOK_GUI = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/book.png");
    public static final List<PacketAuraChunk> PENDING_AURA_CHUNKS = new ArrayList<>();
    private static final ItemStack ITEM_FRAME = new ItemStack(Items.ITEM_FRAME);
    private static final ItemStack DISPENSER = new ItemStack(Blocks.DISPENSER);
    private static final Map<ResourceLocation, Tuple<ItemStack, Boolean>> SHOWING_EFFECTS = new HashMap<>();
    private static ItemStack heldCache = ItemStack.EMPTY;
    private static ItemStack heldEye = ItemStack.EMPTY;
    private static ItemStack heldOcular = ItemStack.EMPTY;

    @SubscribeEvent
    public void onDebugRender(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.renderDebug && ModConfig.instance.debugText.get()) {
            String prefix = ChatFormatting.GREEN + "[" + NaturesAura.MOD_NAME + "]" + ChatFormatting.RESET + " ";
            List<String> left = event.getLeft();
            if (mc.player.isCreative()) {
                left.add("");
                MutableInt amount = new MutableInt(IAuraChunk.DEFAULT_AURA);
                MutableInt spots = new MutableInt();
                MutableInt chunks = new MutableInt();
                IAuraChunk.getSpotsInArea(mc.level, mc.player.blockPosition(), 35, (blockPos, drainSpot) -> {
                    spots.increment();
                    amount.add(drainSpot);
                });
                Helper.getAuraChunksWithSpotsInArea(mc.level, mc.player.blockPosition(), 35, c -> chunks.increment());
                NumberFormat format = NumberFormat.getInstance();
                left.add(prefix + "A: " + format.format(amount.intValue()) + " (S: " + spots.intValue() + ", C: " + chunks.intValue() + ")");
                left.add(prefix + "AT: " + IAuraType.forLevel(mc.level).getName());
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            heldCache = ItemStack.EMPTY;
            heldEye = ItemStack.EMPTY;
            heldOcular = ItemStack.EMPTY;

            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                ItemRangeVisualizer.clear();
                PENDING_AURA_CHUNKS.clear();
            } else {
                PENDING_AURA_CHUNKS.removeIf(next -> next.tryHandle(mc.level));

                if (!mc.isPaused()) {
                    if (mc.level.getGameTime() % 20 == 0) {
                        int amount = Mth.floor(190 * ModConfig.instance.excessParticleAmount.get());
                        for (int i = 0; i < amount; i++) {
                            int x = Mth.floor(mc.player.getX()) + mc.level.random.nextInt(64) - 32;
                            int z = Mth.floor(mc.player.getZ()) + mc.level.random.nextInt(64) - 32;
                            BlockPos pos = new BlockPos(x, mc.level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, z);
                            BlockState state = mc.level.getBlockState(pos);
                            Block block = state.getBlock();
                            if (block instanceof BonemealableBlock || block instanceof IPlantable || block instanceof LeavesBlock || block instanceof MyceliumBlock) {
                                int excess = IAuraChunk.triangulateAuraInArea(mc.level, pos, 45) - IAuraChunk.DEFAULT_AURA;
                                if (excess > 0) {
                                    int chance = Math.max(10, 50 - excess / 25000);
                                    if (mc.level.random.nextInt(chance) <= 0)
                                        NaturesAuraAPI.instance().spawnMagicParticle(
                                                pos.getX() + mc.level.random.nextFloat(),
                                                pos.getY() + 0.5F,
                                                pos.getZ() + mc.level.random.nextFloat(),
                                                mc.level.random.nextGaussian() * 0.01F,
                                                mc.level.random.nextFloat() * 0.025F,
                                                mc.level.random.nextGaussian() * 0.01F,
                                                block instanceof MyceliumBlock ? 0x875ca1 : BiomeColors.getAverageGrassColor(mc.level, pos),
                                                Math.min(2F, 1F + mc.level.random.nextFloat() * (excess / 30000F)),
                                                Math.min(300, 100 + mc.level.random.nextInt(excess / 3000 + 1)),
                                                0F, false, true);
                                }
                            }
                        }
                    }

                    if (Helper.isHoldingItem(mc.player, ModItems.RANGE_VISUALIZER) && mc.level.getGameTime() % 5 == 0) {
                        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
                        inst.setParticleSpawnRange(512);
                        inst.setParticleDepth(false);
                        for (BlockPos pos : ItemRangeVisualizer.VISUALIZED_RAILS.get(mc.level.dimension().location())) {
                            NaturesAuraAPI.instance().spawnMagicParticle(
                                    pos.getX() + mc.level.random.nextFloat(),
                                    pos.getY() + mc.level.random.nextFloat(),
                                    pos.getZ() + mc.level.random.nextFloat(),
                                    0F, 0F, 0F, 0xe0faff, mc.level.random.nextFloat() * 5 + 1, 100, 0F, false, true);
                        }
                        inst.setParticleDepth(true);
                        inst.setParticleSpawnRange(32);
                    }

                    heldCache = Helper.getEquippedItem(s -> s.getItem() instanceof ItemAuraCache, mc.player);
                    heldEye = Helper.getEquippedItem(s -> s.getItem() == ModItems.EYE, mc.player);
                    heldOcular = Helper.getEquippedItem(s -> s.getItem() == ModItems.EYE_IMPROVED, mc.player);

                    if (!heldOcular.isEmpty() && mc.level.getGameTime() % 20 == 0) {
                        SHOWING_EFFECTS.clear();
                        Helper.getAuraChunksWithSpotsInArea(mc.level, mc.player.blockPosition(), 100,
                                chunk -> chunk.getActiveEffectIcons(mc.player, SHOWING_EFFECTS));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLevelRender(RenderLevelLastEvent event) {
        // TODO GL-based in-world rendering
/*        Minecraft mc = Minecraft.getInstance();

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(event.getMatrixStack().getLast().getMatrix());

        ActiveRenderInfo info = mc.gameRenderer.getActiveRenderInfo();
        Vector3d view = info.getProjectedView();
        GL11.glTranslated(-view.getX(), -view.getY(), -view.getZ());

        if (mc.gameSettings.showDebugInfo && mc.player.isCreative() && ModConfig.instance.debugLevel.get()) {
            Map<BlockPos, Integer> spots = new HashMap<>();
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBegin(GL11.GL_QUADS);
            IAuraChunk.getSpotsInArea(mc.level, mc.player.getPosition(), 64, (pos, spot) -> {
                spots.put(pos, spot);

                RenderSystem.color4f(spot > 0 ? 0F : 1F, spot > 0 ? 1F : 0F, 0F, 0.35F);
                Helper.renderWeirdBox(pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
            });
            GL11.glEnd();
            GL11.glPopAttrib();

            float scale = 0.03F;
            NumberFormat format = NumberFormat.getInstance();
            RenderSystem.scalef(scale, scale, scale);
            for (Map.Entry<BlockPos, Integer> spot : spots.entrySet()) {
                BlockPos pos = spot.getKey();
                RenderSystem.pushMatrix();
                RenderSystem.translated((pos.getX() + 0.1) / scale, (pos.getY() + 1.001) / scale, (pos.getZ() + 0.1) / scale);
                RenderSystem.rotatef(90F, 1F, 0F, 0F);
                RenderSystem.scalef(0.65F, 0.65F, 0.65F);
                mc.fontRenderer.drawString(new MatrixStack(), format.format(spot.getValue()), 0, 0, 0);
                RenderSystem.popMatrix();
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }

        if (Helper.isHoldingItem(mc.player, ModItems.RANGE_VISUALIZER)) {
            RegistryKey<Level> dim = mc.level.func_234923_W_();
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBegin(GL11.GL_QUADS);
            for (BlockPos pos : ItemRangeVisualizer.VISUALIZED_BLOCKS.get(dim.func_240901_a_())) {
                if (!mc.level.isBlockLoaded(pos))
                    continue;
                BlockState state = mc.level.getBlockState(pos);
                Block block = state.getBlock();
                if (!(block instanceof IVisualizable))
                    continue;
                this.renderVisualize((IVisualizable) block, mc.level, pos);
            }
            for (Entity entity : ItemRangeVisualizer.VISUALIZED_ENTITIES.get(dim.func_240901_a_())) {
                if (!entity.isAlive() || !(entity instanceof IVisualizable))
                    continue;
                this.renderVisualize((IVisualizable) entity, mc.level, entity.getPosition());
            }
            GL11.glEnd();
            GL11.glPopAttrib();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();*/
    }

/*    private void renderVisualize(IVisualizable visualize, Level
            level, BlockPos pos) {
        AABB box = visualize.getVisualizationBounds(level, pos);
        if (box == null)
            return;
        box = box.grow(0.05F);
        int color = visualize.getVisualizationColor(level, pos);
        RenderSystem.color4f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 0.5F);
        Helper.renderWeirdBox(box.minX, box.minY, box.minZ, box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ);
    }*/

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        // TODO raw rendering bleh, should be easy enough to convert to PoseStack stuff
        /*Minecraft mc = Minecraft.getInstance();
        PoseStack stack = event.getMatrixStack();
        if (event.getType() == ElementType.ALL) {
            var res = event.getWindow();
            if (mc.player != null) {
                if (!heldCache.isEmpty()) {
                    IAuraContainer container = heldCache.getCapability(NaturesAuraAPI.capAuraContainer, null).orElse(null);
                    int width = Mth.ceil(container.getStoredAura() / (float) container.getMaxAura() * 80);

                    int conf = ModConfig.instance.cacheBarLocation.get();
                    int x = res.getGuiScaledWidth() / 2 + (conf == 0 ? -173 - (mc.player.getOffhandItem().isEmpty() ? 0 : 29) : 93);
                    int y = res.getScreenHeight() - 8;

                    RenderSystem.pushMatrix();

                    int color = container.getAuraColor();
                    RenderSystem.color4f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 1);
                    mc.getTextureManager().bindTexture(OVERLAYS);
                    if (width < 80)
                        AbstractGui.blit(stack, x + width, y, width, 0, 80 - width, 6, 256, 256);
                    if (width > 0)
                        AbstractGui.blit(stack, x, y, 0, 6, width, 6, 256, 256);

                    float scale = 0.75F;
                    RenderSystem.scalef(scale, scale, scale);
                    String s = heldCache.getDisplayName().getString();
                    mc.fontRenderer.drawStringWithShadow(stack, s, conf == 1 ? x / scale : (x + 80) / scale - mc.fontRenderer.getStringWidth(s), (y - 7) / scale, color);

                    RenderSystem.color4f(1F, 1F, 1F, 1);
                    RenderSystem.popMatrix();
                }

                if (!heldEye.isEmpty() || !heldOcular.isEmpty()) {
                    RenderSystem.pushMatrix();
                    mc.getTextureManager().bindTexture(OVERLAYS);

                    int conf = ModConfig.instance.auraBarLocation.get();
                    if (!mc.gameSettings.showDebugInfo && (conf != 2 || !(mc.currentScreen instanceof ChatScreen))) {
                        int color = IAuraType.forLevel(mc.level).getColor();
                        RenderSystem.color4f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F, 1);

                        int totalAmount = IAuraChunk.triangulateAuraInArea(mc.level, mc.player.getPosition(), 35);
                        float totalPercentage = totalAmount / (IAuraChunk.DEFAULT_AURA * 2F);
                        String text = I18n.format("info." + NaturesAura.MOD_ID + ".aura_in_area");
                        float textScale = 0.75F;

                        int startX = conf % 2 == 0 ? 3 : res.getScaledWidth() - 3 - 6;
                        int startY = conf < 2 ? 10 : (!heldOcular.isEmpty() && (totalPercentage > 1F || totalPercentage < 0) ? -26 : 0) + res.getScaledHeight() - 60;
                        float plusOffX = conf % 2 == 0 ? 7 : -1 - 6;
                        float textX = conf % 2 == 0 ? 3 : res.getScaledWidth() - 3 - mc.fontRenderer.getStringWidth(text) * textScale;
                        float textY = conf < 2 ? 3 : res.getScaledHeight() - 3 - 6;

                        int tHeight = Mth.ceil(Mth.clamp(totalPercentage, 0F, 1F) * 50);
                        int y = !heldOcular.isEmpty() && totalPercentage > 1F ? startY + 26 : startY;
                        if (tHeight < 50)
                            AbstractGui.blit(stack, startX, y, 6, 12, 6, 50 - tHeight, 256, 256);
                        if (tHeight > 0)
                            AbstractGui.blit(stack, startX, y + 50 - tHeight, 0, 12 + 50 - tHeight, 6, tHeight, 256, 256);

                        if (!heldOcular.isEmpty()) {
                            int topHeight = Mth.ceil(Mth.clamp((totalPercentage - 1F) * 2F, 0F, 1F) * 25);
                            if (topHeight > 0) {
                                if (topHeight < 25)
                                    AbstractGui.blit(stack, startX, startY, 18, 12, 6, 25 - topHeight, 256, 256);
                                AbstractGui.blit(stack, startX, startY + 25 - topHeight, 12, 12 + 25 - topHeight, 6, topHeight, 256, 256);
                            }
                            int bottomHeight = Mth.floor(Mth.clamp((totalPercentage + 1F) * 2F - 1F, 0F, 1F) * 25);
                            if (bottomHeight < 25) {
                                AbstractGui.blit(stack, startX, startY + 51, 18, 12, 6, 25 - bottomHeight, 256, 256);
                                if (bottomHeight > 0)
                                    AbstractGui.blit(stack, startX, startY + 51 + 25 - bottomHeight, 12, 12 + 25 - bottomHeight, 6, bottomHeight, 256, 256);
                            }
                        }

                        if (totalPercentage > (heldOcular.isEmpty() ? 1F : 1.5F))
                            mc.fontRenderer.drawStringWithShadow(stack, "+", startX + plusOffX, startY - 0.5F, color);
                        if (totalPercentage < (heldOcular.isEmpty() ? 0F : -0.5F))
                            mc.fontRenderer.drawStringWithShadow(stack, "-", startX + plusOffX, startY - 0.5F + (heldOcular.isEmpty() ? 44 : 70), color);

                        RenderSystem.pushMatrix();
                        RenderSystem.scalef(textScale, textScale, textScale);
                        mc.fontRenderer.drawStringWithShadow(stack, text, textX / textScale, textY / textScale, color);
                        RenderSystem.popMatrix();

                        if (!heldOcular.isEmpty()) {
                            float scale = 0.75F;
                            RenderSystem.pushMatrix();
                            RenderSystem.scalef(scale, scale, scale);
                            int stackX = conf % 2 == 0 ? 10 : res.getScaledWidth() - 22;
                            int stackY = conf < 2 ? 15 : res.getScaledHeight() - 55;
                            for (Tuple<ItemStack, Boolean> effect : SHOWING_EFFECTS.values()) {
                                int theX = (int) (stackX / scale);
                                int theY = (int) (stackY / scale);
                                ItemStack itemStack = effect.getA();
                                Helper.renderItemInGui(itemStack, theX, theY, 1F);
                                if (effect.getB()) {
                                    GlStateManager.disableDepthTest();
                                    mc.getTextureManager().bindTexture(OVERLAYS);
                                    AbstractGui.blit(stack, theX, theY, 240, 0, 16, 16, 256, 256);
                                    GlStateManager.enableDepthTest();
                                }
                                stackY += 8;
                            }
                            RenderSystem.popMatrix();
                        }
                    }

                    if (mc.objectMouseOver instanceof BlockRayTraceResult) {
                        BlockPos pos = ((BlockRayTraceResult) mc.objectMouseOver).getPos();
                        if (pos != null) {
                            BlockEntity tile = mc.level.getBlockEntity(pos);
                            IAuraContainer container;
                            int x = res.getScaledWidth() / 2;
                            int y = res.getScaledHeight() / 2;
                            if (tile != null && (container = tile.getCapability(NaturesAuraAPI.capAuraContainer, null).orElse(null)) != null) {
                                BlockState state = mc.level.getBlockState(pos);
                                ItemStack blockStack = state.getBlock().getPickBlock(state, mc.objectMouseOver, mc.level, pos, mc.player);
                                this.drawContainerInfo(stack, container.getStoredAura(), container.getMaxAura(), container.getAuraColor(),
                                        mc, res, 35, blockStack.getDisplayName().getString(), null);

                                if (tile instanceof BlockEntityNatureAltar) {
                                    ItemStack tileStack = ((BlockEntityNatureAltar) tile).getItemHandler().getStackInSlot(0);
                                    if (!tileStack.isEmpty()) {
                                        IAuraContainer stackCont = tileStack.getCapability(NaturesAuraAPI.capAuraContainer, null).orElse(null);
                                        if (stackCont != null) {
                                            this.drawContainerInfo(stack, stackCont.getStoredAura(), stackCont.getMaxAura(), stackCont.getAuraColor(),
                                                    mc, res, 55, tileStack.getDisplayName().getString(), null);
                                        }
                                    }
                                }
                            } else if (tile instanceof BlockEntityRFConverter) {
                                EnergyStorage storage = ((BlockEntityRFConverter) tile).storage;
                                this.drawContainerInfo(stack, storage.getEnergyStored(), storage.getMaxEnergyStored(), 0xcc4916,
                                        mc, res, 35, I18n.format("block.naturesaura.rf_converter"),
                                        storage.getEnergyStored() + " / " + storage.getMaxEnergyStored() + " RF");
                            } else if (tile instanceof BlockEntityGratedChute) {
                                BlockEntityGratedChute chute = (BlockEntityGratedChute) tile;
                                ItemStack itemStack = chute.getItemHandler().getStackInSlot(0);

                                if (itemStack.isEmpty())
                                    mc.fontRenderer.drawStringWithShadow(stack,
                                            TextFormatting.GRAY.toString() + TextFormatting.ITALIC + I18n.format("info.naturesaura.empty"),
                                            x + 5, y - 11, 0xFFFFFF);
                                else
                                    Helper.renderItemInGui(itemStack, x + 2, y - 18, 1F);

                                Helper.renderItemInGui(ITEM_FRAME, x - 24, y - 24, 1F);
                                mc.getTextureManager().bindTexture(OVERLAYS);
                                int u = chute.isBlacklist ? 240 : 224;
                                GlStateManager.disableDepthTest();
                                AbstractGui.blit(stack, x - 18, y - 18, u, 0, 16, 16, 256, 256);
                                GlStateManager.enableDepthTest();
                            } else if (tile instanceof BlockEntityItemDistributor) {
                                BlockEntityItemDistributor distributor = (BlockEntityItemDistributor) tile;
                                Helper.renderItemInGui(DISPENSER, x - 24, y - 24, 1F);
                                mc.getTextureManager().bindTexture(OVERLAYS);
                                int u = !distributor.isRandomMode ? 240 : 224;
                                GlStateManager.disableDepthTest();
                                AbstractGui.blit(stack, x - 18, y - 18, u, 0, 16, 16, 256, 256);
                                GlStateManager.enableDepthTest();
                            } else if (tile instanceof BlockEntityAuraTimer) {
                                BlockEntityAuraTimer timer = (BlockEntityAuraTimer) tile;
                                ItemStack itemStack = timer.getItemHandler().getStackInSlot(0);
                                if (!itemStack.isEmpty()) {
                                    Helper.renderItemInGui(itemStack, x - 20, y - 20, 1);
                                    mc.fontRenderer.drawStringWithShadow(stack, TextFormatting.GRAY + this.createTimeString(timer.getTotalTime()), x + 5, y - 11, 0xFFFFFF);
                                    mc.fontRenderer.drawStringWithShadow(stack, TextFormatting.GRAY + I18n.format("info.naturesaura.remaining", this.createTimeString(timer.getTimeLeft())), x + 5, y + 3, 0xFFFFFF);
                                }
                            }
                        }
                    }

                    RenderSystem.color4f(1F, 1F, 1F, 1);
                    RenderSystem.popMatrix();
                }
            }
        }*/
    }

    private String createTimeString(int totalTicks) {
        int ticks = totalTicks % 20;
        int seconds = totalTicks / 20 % 60;
        int minutes = totalTicks / 20 / 60 % 60;
        int hours = totalTicks / 20 / 60 / 60;
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, ticks);
    }

/*    private void drawContainerInfo(PoseStack stack, int stored, int max, int color, Minecraft mc, MainWindow res, int yOffset, String name, String textBelow) {
        RenderSystem.color3f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F);

        int x = res.getScaledWidth() / 2 - 40;
        int y = res.getScaledHeight() / 2 + yOffset;
        int width = Mth.ceil(stored / (float) max * 80);

        mc.getTextureManager().bindTexture(OVERLAYS);
        if (width < 80)
            AbstractGui.blit(stack, x + width, y, width, 0, 80 - width, 6, 256, 256);
        if (width > 0)
            AbstractGui.blit(stack, x, y, 0, 6, width, 6, 256, 256);

        mc.fontRenderer.drawStringWithShadow(stack, name, x + 40 - mc.fontRenderer.getStringWidth(name) / 2F, y - 9, color);

        if (textBelow != null)
            mc.fontRenderer.drawStringWithShadow(stack, textBelow, x + 40 - mc.fontRenderer.getStringWidth(textBelow) / 2F, y + 7, color);
    }*/
}
