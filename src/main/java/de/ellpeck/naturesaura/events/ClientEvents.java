package de.ellpeck.naturesaura.events;

import baubles.api.BaublesApi;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import de.ellpeck.naturesaura.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableInt;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEvents {

    public static final ResourceLocation OVERLAYS = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/overlays.png");

    @SubscribeEvent
    public void onDebugRender(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.showDebugInfo) {
            String prefix = TextFormatting.GREEN + "[" + NaturesAura.MOD_NAME + "]" + TextFormatting.RESET + " ";
            List<String> left = event.getLeft();
            left.add("");
            left.add(prefix + "Particles: " + ParticleHandler.getParticleAmount());

            if (mc.player.capabilities.isCreativeMode) {
                left.add(prefix + "Aura:");
                MutableInt amount = new MutableInt(AuraChunk.DEFAULT_AURA);
                MutableInt spots = new MutableInt();
                AuraChunk.getSpotsInArea(mc.world, mc.player.getPosition(), 15, ((blockPos, drainSpot) -> {
                    spots.increment();
                    amount.add(drainSpot.intValue());
                    left.add(prefix + drainSpot.intValue() + " @ " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
                }));
                left.add(prefix + "Total: " + amount.intValue() + " in " + spots.intValue() + " spots");
            }
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        ParticleHandler.renderParticles(event.getPartialTicks());
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event) {
        event.getMap().registerSprite(ParticleMagic.TEXTURE);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isGamePaused()) {
            ParticleHandler.updateParticles();
        }
        if (mc.world == null) {
            ParticleHandler.clearParticles();
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.getType() == ElementType.ALL) {
            ScaledResolution res = event.getResolution();
            if (mc.player != null) {
                ItemStack cache = ItemStack.EMPTY;
                ItemStack eye = ItemStack.EMPTY;

                if (Compat.baubles) {
                    IItemHandler baubles = BaublesApi.getBaublesHandler(mc.player);
                    for (int i = 0; i < baubles.getSlots(); i++) {
                        ItemStack slot = baubles.getStackInSlot(i);
                        if (!slot.isEmpty()) {
                            if (slot.getItem() == ModItems.AURA_CACHE) {
                                cache = slot;
                                break;
                            } else if (slot.getItem() == ModItems.EYE) {
                                eye = slot;
                                break;
                            }
                        }
                    }
                }

                if (cache.isEmpty() || eye.isEmpty()) {
                    for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
                        ItemStack slot = mc.player.inventory.getStackInSlot(i);
                        if (!slot.isEmpty()) {
                            if (slot.getItem() == ModItems.AURA_CACHE) {
                                cache = slot;
                                break;
                            } else if (slot.getItem() == ModItems.EYE && i <= 8) {
                                eye = slot;
                                break;
                            }
                        }
                    }
                }

                if (!cache.isEmpty()) {
                    IAuraContainer container = cache.getCapability(Capabilities.auraContainer, null);
                    int width = MathHelper.ceil(container.getStoredAura() / (float) container.getMaxAura() * 80);
                    int x = res.getScaledWidth() / 2 - 173 - (mc.player.getHeldItemOffhand().isEmpty() ? 0 : 29);
                    int y = res.getScaledHeight() - 8;

                    GlStateManager.pushMatrix();

                    int color = container.getAuraColor();
                    GlStateManager.color((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F);
                    mc.getTextureManager().bindTexture(OVERLAYS);
                    if (width < 80)
                        Gui.drawModalRectWithCustomSizedTexture(x + width, y, width, 0, 80 - width, 6, 256, 256);
                    if (width > 0)
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 6, width, 6, 256, 256);

                    float scale = 0.75F;
                    GlStateManager.scale(scale, scale, scale);
                    String s = cache.getDisplayName();
                    mc.fontRenderer.drawString(s, (x + 80) / scale - mc.fontRenderer.getStringWidth(s), (y - 7) / scale, color, true);

                    GlStateManager.color(1F, 1F, 1F);
                    GlStateManager.popMatrix();
                }

                if (!eye.isEmpty()) {
                    GlStateManager.pushMatrix();
                    mc.getTextureManager().bindTexture(OVERLAYS);

                    if (!mc.gameSettings.showDebugInfo) {
                        GlStateManager.color(0.8F, 0.25F, 0.25F);
                        float totalPercentage = AuraChunk.getAuraInArea(mc.world, mc.player.getPosition(), 15) / (AuraChunk.DEFAULT_AURA * 2F);
                        int tHeight = MathHelper.ceil(MathHelper.clamp(totalPercentage, 0F, 1F) * 50);
                        if (tHeight < 50)
                            Gui.drawModalRectWithCustomSizedTexture(3, 10, 6, 12, 6, 50 - tHeight, 256, 256);
                        if (tHeight > 0)
                            Gui.drawModalRectWithCustomSizedTexture(3, 10 + 50 - tHeight, 0, 12 + 50 - tHeight, 6, tHeight, 256, 256);

                        if (totalPercentage > 1F)
                            mc.fontRenderer.drawString("+", 10F, 9.5F, 0xBB3333, true);
                        if (totalPercentage < 0F)
                            mc.fontRenderer.drawString("-", 10F, 53.5F, 0xBB3333, true);

                        GlStateManager.pushMatrix();
                        float scale = 0.75F;
                        GlStateManager.scale(scale, scale, scale);
                        mc.fontRenderer.drawString(I18n.format("info." + NaturesAura.MOD_ID + ".aura_in_area"), 3 / scale, 3 / scale, 0xBB3333, true);
                        GlStateManager.popMatrix();
                    }

                    if (mc.objectMouseOver != null) {
                        BlockPos pos = mc.objectMouseOver.getBlockPos();
                        if (pos != null) {
                            TileEntity tile = mc.world.getTileEntity(pos);
                            if (tile != null && tile.hasCapability(Capabilities.auraContainer, null)) {
                                IAuraContainer container = tile.getCapability(Capabilities.auraContainer, null);

                                IBlockState state = mc.world.getBlockState(pos);
                                ItemStack blockStack = state.getBlock().getPickBlock(state, mc.objectMouseOver, mc.world, pos, mc.player);
                                this.drawContainerInfo(container, mc, res, 35, blockStack.getDisplayName());

                                if (tile instanceof TileEntityNatureAltar) {
                                    ItemStack tileStack = ((TileEntityNatureAltar) tile).getItemHandler(null).getStackInSlot(0);
                                    if (!tileStack.isEmpty() && tileStack.hasCapability(Capabilities.auraContainer, null)) {
                                        IAuraContainer stackContainer = tileStack.getCapability(Capabilities.auraContainer, null);
                                        this.drawContainerInfo(stackContainer, mc, res, 55, tileStack.getDisplayName());
                                    }
                                }
                            }
                        }
                    }

                    GlStateManager.color(1F, 1F, 1F);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private void drawContainerInfo(IAuraContainer container, Minecraft mc, ScaledResolution res, int yOffset, String name) {
        int color = container.getAuraColor();
        GlStateManager.color((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F);

        int x = res.getScaledWidth() / 2 - 40;
        int y = res.getScaledHeight() / 2 + yOffset;
        int width = MathHelper.ceil(container.getStoredAura() / (float) container.getMaxAura() * 80);

        mc.getTextureManager().bindTexture(OVERLAYS);
        if (width < 80)
            Gui.drawModalRectWithCustomSizedTexture(x + width, y, width, 0, 80 - width, 6, 256, 256);
        if (width > 0)
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 6, width, 6, 256, 256);

        mc.fontRenderer.drawString(name, x + 40 - mc.fontRenderer.getStringWidth(name) / 2F, y - 9, color, true);
    }
}
