package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.IAuraContainer;
import de.ellpeck.naturesaura.aura.IAuraContainerProvider;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
        if (event.getType() == ElementType.ALL && mc.currentScreen == null) {
            ScaledResolution res = event.getResolution();
            if (mc.player != null) {
                ItemStack stack = mc.player.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() == ModItems.EYE) {
                    int maxAura = 0;
                    int aura = 0;
                    for (TileEntity tile : Helper.getTileEntitiesInArea(mc.world, mc.player.getPosition(), 15)) {
                        if (tile instanceof IAuraContainerProvider) {
                            IAuraContainerProvider provider = (IAuraContainerProvider) tile;
                            if (!provider.isArtificial()) {
                                IAuraContainer container = provider.container();
                                maxAura += container.getMaxAura();
                                aura += container.getStoredAura();
                            }
                        }
                    }
                    String area = "Aura in the area: " + aura + " / " + maxAura;
                    mc.fontRenderer.drawString(area, 5, 5, 0xFFFFFF, true);

                    if (mc.objectMouseOver != null) {
                        BlockPos pos = mc.objectMouseOver.getBlockPos();
                        if (pos != null) {
                            TileEntity tile = mc.world.getTileEntity(pos);
                            if (tile instanceof IAuraContainerProvider) {
                                IAuraContainer container = ((IAuraContainerProvider) tile).container();
                                String s = "Aura stored: " + container.getStoredAura() + " / " + container.getMaxAura();
                                mc.fontRenderer.drawString(s,
                                        (res.getScaledWidth() - mc.fontRenderer.getStringWidth(s)) / 2, res.getScaledHeight() / 4 * 3,
                                        container.getAuraColor(), true);
                            }
                        }
                    }
                }
            }
        }
    }
}
