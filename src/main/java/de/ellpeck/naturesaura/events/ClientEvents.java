package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
        if (Minecraft.getInstance().gameSettings.showDebugInfo) {
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
        event.getMap().func_174942_a(ParticleMagic.TEXTURE);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isGamePaused()) {
            ParticleHandler.updateParticles();
        }
        if (mc.world == null) {
            ParticleHandler.clearParticles();
        }
    }
}
