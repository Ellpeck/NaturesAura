package de.ellpeck.naturesaura.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public final class ParticleHandler {

    private static final List<Particle> PARTICLES = new ArrayList<>();

    public static void spawnParticle(Particle particle, double x, double y, double z, int range) {
        if (Minecraft.getInstance().player.getDistanceSq(x, y, z) <= range * range) {
            PARTICLES.add(particle);
        }
    }

    public static void updateParticles() {
        for (int i = 0; i < PARTICLES.size(); i++) {
            boolean remove = false;

            Particle particle = PARTICLES.get(i);
            if (particle != null) {
                particle.tick();

                if (!particle.isAlive()) {
                    remove = true;
                }
            } else {
                remove = true;
            }

            if (remove) {
                PARTICLES.remove(i);
                i--;
            }
        }
    }

    public static void renderParticles(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        EntityPlayer player = mc.player;

        if (player != null) {
            float x = ActiveRenderInfo.getRotationX();
            float z = ActiveRenderInfo.getRotationZ();
            float yz = ActiveRenderInfo.getRotationYZ();
            float xy = ActiveRenderInfo.getRotationXY();
            float xz = ActiveRenderInfo.getRotationXZ();

            Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            Particle.cameraViewDir = player.getLook(partialTicks);

            GlStateManager.pushMatrix();

            GlStateManager.enableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(516, 0.003921569F);
            GlStateManager.disableCull();

            GlStateManager.depthMask(false);

            mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tessy = Tessellator.getInstance();
            BufferBuilder buffer = tessy.getBuffer();

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : PARTICLES) {
                if (particle != null) {
                    particle.renderParticle(buffer, player, partialTicks, x, xz, z, yz, xy);
                }
            }

            tessy.draw();

            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);

            GlStateManager.popMatrix();
        }
    }

    public static int getParticleAmount() {
        return PARTICLES.size();
    }

    public static void clearParticles() {
        if (!PARTICLES.isEmpty()) {
            PARTICLES.clear();
        }
    }
}