package de.ellpeck.naturesaura.events;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.*;
import de.ellpeck.naturesaura.items.ItemAuraCache;
import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.energy.EnergyStorage;
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
    private static BlockPos hoveringAuraSpot;

    @SubscribeEvent
    public void onDebugRender(RenderGameOverlayEvent.Text event) {
        var mc = Minecraft.getInstance();
        if (mc.options.renderDebug && ModConfig.instance.debugText.get()) {
            var prefix = ChatFormatting.GREEN + "[" + NaturesAura.MOD_NAME + "]" + ChatFormatting.RESET + " ";
            List<String> left = event.getLeft();
            if (mc.player.isCreative()) {
                left.add("");
                var amount = new MutableInt(IAuraChunk.DEFAULT_AURA);
                var spots = new MutableInt();
                var chunks = new MutableInt();
                IAuraChunk.getSpotsInArea(mc.level, mc.player.blockPosition(), 35, (blockPos, drainSpot) -> {
                    spots.increment();
                    amount.add(drainSpot);
                });
                Helper.getAuraChunksWithSpotsInArea(mc.level, mc.player.blockPosition(), 35, c -> chunks.increment());
                var format = NumberFormat.getInstance();
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

            var mc = Minecraft.getInstance();
            if (mc.level == null) {
                ItemRangeVisualizer.clear();
                PENDING_AURA_CHUNKS.clear();
            } else {
                PENDING_AURA_CHUNKS.removeIf(next -> next.tryHandle(mc.level));

                if (!mc.isPaused()) {
                    if (mc.level.getGameTime() % 20 == 0) {
                        var amount = Mth.floor(190 * ModConfig.instance.excessParticleAmount.get());
                        for (var i = 0; i < amount; i++) {
                            var x = Mth.floor(mc.player.getX()) + mc.level.random.nextInt(64) - 32;
                            var z = Mth.floor(mc.player.getZ()) + mc.level.random.nextInt(64) - 32;
                            var pos = new BlockPos(x, mc.level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, z);
                            var state = mc.level.getBlockState(pos);
                            var block = state.getBlock();
                            if (block instanceof BonemealableBlock || block instanceof IPlantable || block instanceof LeavesBlock || block instanceof MyceliumBlock) {
                                var excess = IAuraChunk.triangulateAuraInArea(mc.level, pos, 45) - IAuraChunk.DEFAULT_AURA;
                                if (excess > 0) {
                                    var chance = Math.max(10, 50 - excess / 25000);
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
                        var inst = NaturesAuraAPI.instance();
                        inst.setParticleSpawnRange(512);
                        inst.setParticleDepth(false);
                        for (var pos : ItemRangeVisualizer.VISUALIZED_RAILS.get(mc.level.dimension().location())) {
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
        var mc = Minecraft.getInstance();
        var view = mc.gameRenderer.getMainCamera().getPosition();
        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.getBuilder();

        mc.getProfiler().push(NaturesAura.MOD_ID + ":onLevelRender");

        RenderSystem.enableDepthTest();
        var mv = RenderSystem.getModelViewStack();
        mv.pushPose();
        mv.mulPoseMatrix(event.getPoseStack().last().pose());
        mv.translate(-view.x, -view.y, -view.z);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableBlend();

        // aura spot debug
        hoveringAuraSpot = null;
        if (mc.options.renderDebug && mc.player.isCreative() && ModConfig.instance.debugLevel.get()) {
            var playerEye = mc.player.getEyePosition(event.getPartialTick());
            var playerView = mc.player.getViewVector(event.getPartialTick()).normalize();
            var range = mc.gameMode.getPickRange();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            IAuraChunk.getSpotsInArea(mc.level, mc.player.blockPosition(), 64, (pos, spot) -> {
                Helper.renderWeirdBox(buffer, pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1, spot > 0 ? 0F : 1F, spot > 0 ? 1F : 0F, 0F, 0.35F);
                // dirty raytrace to see if we're looking at roughly this spot
                if (playerEye.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= range * range) {
                    for (var d = 0F; d <= range; d += 0.5F) {
                        if (pos.equals(new BlockPos(playerEye.add(playerView.scale(d))))) {
                            hoveringAuraSpot = pos;
                            break;
                        }
                    }
                }

            });
            tesselator.end();
        }

        // range visualizer
        if (Helper.isHoldingItem(mc.player, ModItems.RANGE_VISUALIZER)) {
            RenderSystem.disableCull();
            var dim = mc.level.dimension().location();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (var pos : ItemRangeVisualizer.VISUALIZED_BLOCKS.get(dim)) {
                if (!mc.level.isLoaded(pos))
                    continue;
                var state = mc.level.getBlockState(pos);
                var block = state.getBlock();
                if (!(block instanceof IVisualizable))
                    continue;
                this.renderVisualize(buffer, (IVisualizable) block, mc.level, pos);
            }
            for (var entity : ItemRangeVisualizer.VISUALIZED_ENTITIES.get(dim)) {
                if (!entity.isAlive() || !(entity instanceof IVisualizable))
                    continue;
                this.renderVisualize(buffer, (IVisualizable) entity, mc.level, entity.blockPosition());
            }
            tesselator.end();
            RenderSystem.enableCull();
        }

        mv.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableBlend();

        mc.getProfiler().pop();
    }

    private void renderVisualize(BufferBuilder buffer, IVisualizable visualize, Level level, BlockPos pos) {
        var box = visualize.getVisualizationBounds(level, pos);
        if (box != null) {
            box = box.inflate(0.05F);
            var color = visualize.getVisualizationColor(level, pos);
            Helper.renderWeirdBox(buffer, box.minX, box.minY, box.minZ, box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ, (color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 0.5F);
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        var mc = Minecraft.getInstance();
        var stack = event.getMatrixStack();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            var res = event.getWindow();
            if (mc.player != null) {
                if (!heldCache.isEmpty()) {
                    var container = heldCache.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER, null).orElse(null);
                    var width = Mth.ceil(container.getStoredAura() / (float) container.getMaxAura() * 80);

                    int conf = ModConfig.instance.cacheBarLocation.get();
                    var x = res.getGuiScaledWidth() / 2 + (conf == 0 ? -173 - (mc.player.getOffhandItem().isEmpty() ? 0 : 29) : 93);
                    var y = res.getGuiScaledHeight() - 8;

                    stack.pushPose();

                    var color = container.getAuraColor();
                    RenderSystem.setShaderColor((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 1);
                    RenderSystem.setShaderTexture(0, OVERLAYS);
                    if (width < 80)
                        Screen.blit(stack, x + width, y, width, 0, 80 - width, 6, 256, 256);
                    if (width > 0)
                        Screen.blit(stack, x, y, 0, 6, width, 6, 256, 256);

                    var scale = 0.75F;
                    stack.pushPose();
                    stack.scale(scale, scale, scale);
                    var s = heldCache.getHoverName().getString();
                    mc.font.drawShadow(stack, s, conf == 1 ? x / scale : (x + 80) / scale - mc.font.width(s), (y - 7) / scale, color);
                    stack.popPose();

                    RenderSystem.setShaderColor(1F, 1F, 1F, 1);
                    stack.pushPose();
                }

                if (!heldEye.isEmpty() || !heldOcular.isEmpty()) {
                    stack.pushPose();
                    RenderSystem.setShaderTexture(0, OVERLAYS);

                    int conf = ModConfig.instance.auraBarLocation.get();
                    if (!mc.options.renderDebug && (conf != 2 || !(mc.screen instanceof ChatScreen))) {
                        var color = IAuraType.forLevel(mc.level).getColor();
                        RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F, 1);

                        var totalAmount = IAuraChunk.triangulateAuraInArea(mc.level, mc.player.blockPosition(), 35);
                        var totalPercentage = totalAmount / (IAuraChunk.DEFAULT_AURA * 2F);
                        var text = I18n.get("info." + NaturesAura.MOD_ID + ".aura_in_area");
                        var textScale = 0.75F;

                        var startX = conf % 2 == 0 ? 3 : res.getGuiScaledWidth() - 3 - 6;
                        var startY = conf < 2 ? 10 : (!heldOcular.isEmpty() && (totalPercentage > 1F || totalPercentage < 0) ? -26 : 0) + res.getGuiScaledHeight() - 60;
                        float plusOffX = conf % 2 == 0 ? 7 : -1 - 6;
                        var textX = conf % 2 == 0 ? 3 : res.getGuiScaledWidth() - 3 - mc.font.width(text) * textScale;
                        float textY = conf < 2 ? 3 : res.getGuiScaledHeight() - 3 - 6;

                        var tHeight = Mth.ceil(Mth.clamp(totalPercentage, 0F, 1F) * 50);
                        var y = !heldOcular.isEmpty() && totalPercentage > 1F ? startY + 26 : startY;
                        if (tHeight < 50)
                            Screen.blit(stack, startX, y, 6, 12, 6, 50 - tHeight, 256, 256);
                        if (tHeight > 0)
                            Screen.blit(stack, startX, y + 50 - tHeight, 0, 12 + 50 - tHeight, 6, tHeight, 256, 256);

                        if (!heldOcular.isEmpty()) {
                            var topHeight = Mth.ceil(Mth.clamp((totalPercentage - 1F) * 2F, 0F, 1F) * 25);
                            if (topHeight > 0) {
                                if (topHeight < 25)
                                    Screen.blit(stack, startX, startY, 18, 12, 6, 25 - topHeight, 256, 256);
                                Screen.blit(stack, startX, startY + 25 - topHeight, 12, 12 + 25 - topHeight, 6, topHeight, 256, 256);
                            }
                            var bottomHeight = Mth.floor(Mth.clamp((totalPercentage + 1F) * 2F - 1F, 0F, 1F) * 25);
                            if (bottomHeight < 25) {
                                Screen.blit(stack, startX, startY + 51, 18, 12, 6, 25 - bottomHeight, 256, 256);
                                if (bottomHeight > 0)
                                    Screen.blit(stack, startX, startY + 51 + 25 - bottomHeight, 12, 12 + 25 - bottomHeight, 6, bottomHeight, 256, 256);
                            }
                        }

                        if (totalPercentage > (heldOcular.isEmpty() ? 1F : 1.5F))
                            mc.font.drawShadow(stack, "+", startX + plusOffX, startY - 0.5F, color);
                        if (totalPercentage < (heldOcular.isEmpty() ? 0F : -0.5F))
                            mc.font.drawShadow(stack, "-", startX + plusOffX, startY - 0.5F + (heldOcular.isEmpty() ? 44 : 70), color);

                        stack.pushPose();
                        stack.scale(textScale, textScale, textScale);
                        mc.font.drawShadow(stack, text, textX / textScale, textY / textScale, color);
                        stack.popPose();

                        if (!heldOcular.isEmpty()) {
                            var scale = 0.75F;
                            stack.pushPose();
                            stack.scale(scale, scale, scale);
                            var stackX = conf % 2 == 0 ? 10 : res.getGuiScaledWidth() - 22;
                            var stackY = conf < 2 ? 15 : res.getGuiScaledHeight() - 55;
                            for (var effect : SHOWING_EFFECTS.values()) {
                                var theX = (int) (stackX / scale);
                                var theY = (int) (stackY / scale);
                                var itemStack = effect.getA();
                                Helper.renderItemInGui(itemStack, theX, theY, 1F);
                                if (effect.getB()) {
                                    RenderSystem.disableDepthTest();
                                    RenderSystem.setShaderTexture(0, OVERLAYS);
                                    Screen.blit(stack, theX, theY, 240, 0, 16, 16, 256, 256);
                                    RenderSystem.enableDepthTest();
                                }
                                stackY += 8;
                            }
                            stack.popPose();
                        }
                    }

                    if (mc.hitResult instanceof BlockHitResult blockHitResult) {
                        var pos = blockHitResult.getBlockPos();
                        if (pos != null) {
                            var tile = mc.level.getBlockEntity(pos);
                            IAuraContainer container;
                            var x = res.getGuiScaledWidth() / 2;
                            var y = res.getGuiScaledHeight() / 2;
                            if (tile != null && (container = tile.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER, null).orElse(null)) != null) {
                                var state = mc.level.getBlockState(pos);
                                var blockStack = state.getBlock().getCloneItemStack(state, blockHitResult, mc.level, pos, mc.player);
                                this.drawContainerInfo(stack, container.getStoredAura(), container.getMaxAura(), container.getAuraColor(),
                                        mc, res, 35, blockStack.getHoverName().getString(), null);

                                if (tile instanceof BlockEntityNatureAltar) {
                                    var tileStack = ((BlockEntityNatureAltar) tile).getItemHandler().getStackInSlot(0);
                                    if (!tileStack.isEmpty()) {
                                        var stackCont = tileStack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER, null).orElse(null);
                                        if (stackCont != null) {
                                            this.drawContainerInfo(stack, stackCont.getStoredAura(), stackCont.getMaxAura(), stackCont.getAuraColor(),
                                                    mc, res, 55, tileStack.getHoverName().getString(), null);
                                        }
                                    }
                                }
                            } else if (tile instanceof BlockEntityRFConverter) {
                                EnergyStorage storage = ((BlockEntityRFConverter) tile).storage;
                                this.drawContainerInfo(stack, storage.getEnergyStored(), storage.getMaxEnergyStored(), 0xcc4916,
                                        mc, res, 35, I18n.get("block.naturesaura.rf_converter"),
                                        storage.getEnergyStored() + " / " + storage.getMaxEnergyStored() + " RF");
                            } else if (tile instanceof BlockEntityGratedChute chute) {
                                var itemStack = chute.getItemHandler().getStackInSlot(0);

                                if (itemStack.isEmpty()) {
                                    mc.font.drawShadow(stack,
                                            ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC + I18n.get("info.naturesaura.empty"),
                                            x + 5, y - 11, 0xFFFFFF);
                                } else {
                                    Helper.renderItemInGui(itemStack, x + 2, y - 18, 1F);
                                }

                                Helper.renderItemInGui(ITEM_FRAME, x - 24, y - 24, 1F);
                                RenderSystem.setShaderTexture(0, OVERLAYS);
                                var u = chute.isBlacklist ? 240 : 224;
                                RenderSystem.disableDepthTest();
                                Screen.blit(stack, x - 18, y - 18, u, 0, 16, 16, 256, 256);
                                RenderSystem.enableDepthTest();
                            } else if (tile instanceof BlockEntityItemDistributor distributor) {
                                Helper.renderItemInGui(DISPENSER, x - 24, y - 24, 1F);
                                RenderSystem.setShaderTexture(0, OVERLAYS);
                                var u = !distributor.isRandomMode ? 240 : 224;
                                RenderSystem.disableDepthTest();
                                Screen.blit(stack, x - 18, y - 18, u, 0, 16, 16, 256, 256);
                                RenderSystem.enableDepthTest();
                            } else if (tile instanceof BlockEntityAuraTimer timer) {
                                var itemStack = timer.getItemHandler().getStackInSlot(0);
                                if (!itemStack.isEmpty()) {
                                    Helper.renderItemInGui(itemStack, x - 20, y - 20, 1);
                                    mc.font.drawShadow(stack, ChatFormatting.GRAY + this.createTimeString(timer.getTotalTime()), x + 5, y - 11, 0xFFFFFF);
                                    mc.font.drawShadow(stack, ChatFormatting.GRAY + I18n.get("info.naturesaura.remaining", this.createTimeString(timer.getTimeLeft())), x + 5, y + 3, 0xFFFFFF);
                                }
                            }
                        }
                    }

                    RenderSystem.setShaderColor(1F, 1F, 1F, 1);
                    stack.popPose();
                }

                if (hoveringAuraSpot != null) {
                    var format = NumberFormat.getInstance();
                    var amount = IAuraChunk.getAuraChunk(mc.level, hoveringAuraSpot).getDrainSpot(hoveringAuraSpot);
                    var color = amount > 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
                    mc.font.drawShadow(stack, color + format.format(amount), res.getGuiScaledWidth() / 2F + 5, res.getGuiScaledHeight() / 2F - 11, 0xFFFFFF);
                }
            }
        }
    }

    private String createTimeString(int totalTicks) {
        var ticks = totalTicks % 20;
        var seconds = totalTicks / 20 % 60;
        var minutes = totalTicks / 20 / 60 % 60;
        var hours = totalTicks / 20 / 60 / 60;
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, ticks);
    }

    private void drawContainerInfo(PoseStack stack, int stored, int max, int color, Minecraft mc, Window res, int yOffset, String name, String textBelow) {
        RenderSystem.setShaderColor((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 1);

        var x = res.getGuiScaledWidth() / 2 - 40;
        var y = res.getGuiScaledHeight() / 2 + yOffset;
        var width = Mth.ceil(stored / (float) max * 80);

        RenderSystem.setShaderTexture(0, OVERLAYS);
        if (width < 80)
            Screen.blit(stack, x + width, y, width, 0, 80 - width, 6, 256, 256);
        if (width > 0)
            Screen.blit(stack, x, y, 0, 6, width, 6, 256, 256);

        mc.font.drawShadow(stack, name, x + 40 - mc.font.width(name) / 2F, y - 9, color);

        if (textBelow != null)
            mc.font.drawShadow(stack, textBelow, x + 40 - mc.font.width(textBelow) / 2F, y + 7, color);
    }
}
