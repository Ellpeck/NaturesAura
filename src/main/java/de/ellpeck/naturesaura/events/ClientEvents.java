package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.IAuraContainer;
import de.ellpeck.naturesaura.aura.IAuraContainerProvider;
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

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEvents {

    private static final ResourceLocation OVERLAYS = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/overlays.png");

    @SubscribeEvent
    public void onDebugRender(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            String prefix = TextFormatting.GREEN + "[" + NaturesAura.MOD_NAME + "]" + TextFormatting.RESET + " ";
            List<String> left = event.getLeft();
            left.add("");
            left.add(prefix + "PartScrn: " + ParticleHandler.getParticleAmount());
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
        if (event.getType() == ElementType.ALL /*&& mc.currentScreen == null*/) {
            ScaledResolution res = event.getResolution();
            if (mc.player != null) {
                ItemStack stack = mc.player.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() == ModItems.EYE) {
                    int maxAura = 0;
                    int aura = 0;
                    int total = 0;

                    for (TileEntity tile : Helper.getTileEntitiesInArea(mc.world, mc.player.getPosition(), 15)) {
                        if (tile instanceof IAuraContainerProvider) {
                            IAuraContainerProvider provider = (IAuraContainerProvider) tile;
                            if (!provider.isArtificial()) {
                                IAuraContainer container = provider.container();
                                maxAura += container.getMaxAura();
                                aura += container.getStoredAura();
                                total++;
                            }
                        }
                    }

                    GlStateManager.pushMatrix();
                    mc.getTextureManager().bindTexture(OVERLAYS);

                    GlStateManager.color(0.8F, 0.25F, 0.25F);
                    float totalPercentage = total / 1500F;
                    int tHeight = MathHelper.ceil(Math.min(1F, totalPercentage) * 75);
                    if (tHeight < 75)
                        Gui.drawModalRectWithCustomSizedTexture(3, 17, 6, 12, 6, 75 - tHeight, 256, 256);
                    if (tHeight > 0)
                        Gui.drawModalRectWithCustomSizedTexture(3, 17 + 75 - tHeight, 0, 12 + 75 - tHeight, 6, tHeight, 256, 256);

                    GlStateManager.color(0.25F, 0.8F, 0.25F);
                    int aHeight = MathHelper.ceil(aura / (float) maxAura * 75);
                    if (aHeight < 75)
                        Gui.drawModalRectWithCustomSizedTexture(12, 17, 6, 12, 6, 75 - aHeight, 256, 256);
                    if (aHeight > 0)
                        Gui.drawModalRectWithCustomSizedTexture(12, 17 + 75 - aHeight, 0, 12 + 75 - aHeight, 6, aHeight, 256, 256);

                    if (mc.objectMouseOver != null) {
                        BlockPos pos = mc.objectMouseOver.getBlockPos();
                        if (pos != null) {
                            TileEntity tile = mc.world.getTileEntity(pos);
                            if (tile instanceof IAuraContainerProvider) {
                                IAuraContainer container = ((IAuraContainerProvider) tile).container();

                                int color = container.getAuraColor();
                                GlStateManager.color((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F);

                                int x = res.getScaledWidth() / 2 - 50;
                                int y = res.getScaledHeight() / 2 + 25;
                                int width = MathHelper.ceil(container.getStoredAura() / (float) container.getMaxAura() * 100F);

                                if (width < 100)
                                    Gui.drawModalRectWithCustomSizedTexture(x + width, y, width, 0, 100 - width, 6, 256, 256);
                                if (width > 0)
                                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 6, width, 6, 256, 256);

                                IBlockState state = mc.world.getBlockState(pos);
                                ItemStack blockStack = state.getBlock().getPickBlock(state, mc.objectMouseOver, mc.world, pos, mc.player);
                                String s = blockStack.getDisplayName();
                                mc.fontRenderer.drawString(s, x + 50 - mc.fontRenderer.getStringWidth(s) / 2F, y - 9, color, true);
                            }
                        }
                    }

                    if (totalPercentage > 1F) {
                        mc.fontRenderer.drawString("+", 3F, 9.5F, 0xBB3333, true);
                    }

                    float scale = 0.75F;
                    GlStateManager.scale(scale, scale, scale);
                    mc.fontRenderer.drawString(I18n.format("info." + NaturesAura.MOD_ID + ".aura_in_area"), 3 / scale, 3 / scale, 0xBB3333, true);
                    mc.fontRenderer.drawString(I18n.format("info." + NaturesAura.MOD_ID + ".aura_percentage"), 12 / scale, 10 / scale, 0x33BB33, true);

                    GlStateManager.popMatrix();
                }
            }
        }
    }
}
