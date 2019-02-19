package de.ellpeck.naturesaura.particles;

import de.ellpeck.naturesaura.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public final class ParticleHandler {

    public static boolean depthEnabled = true;
    public static int range = 32;
    private static final List<Particle> PARTICLES = new ArrayList<>();
    private static final List<Particle> PARTICLES_NO_DEPTH = new ArrayList<>();

    public static void spawnParticle(Supplier<Particle> particle, double x, double y, double z) {
        if (Minecraft.getMinecraft().player.getDistanceSq(x, y, z) <= range * range) {
            Minecraft mc = Minecraft.getMinecraft();
            if (ModConfig.client.respectVanillaParticleSettings) {
                int setting = mc.gameSettings.particleSetting;
                if (setting != 0 &&
                        (setting != 1 || mc.world.rand.nextInt(3) != 0) &&
                        (setting != 2 || mc.world.rand.nextInt(10) != 0))
                    return;
            }
            double setting = ModConfig.client.particleAmount;
            if (setting < 1 && mc.world.rand.nextDouble() > setting)
                return;

            if (depthEnabled)
                PARTICLES.add(particle.get());
            else
                PARTICLES_NO_DEPTH.add(particle.get());
        }
    }

    public static void updateParticles() {
        updateList(PARTICLES);
        updateList(PARTICLES_NO_DEPTH);
    }

    private static void updateList(List<Particle> particles) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.onUpdate();
            if (!particle.isAlive())
                particles.remove(i);
        }
    }

    public static void renderParticles(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
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

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(516, 0.003921569F);
            GlStateManager.disableCull();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            GlStateManager.depthMask(false);

            mc.getTextureManager().bindTexture(ParticleMagic.TEXTURE);
            Tessellator tessy = Tessellator.getInstance();
            BufferBuilder buffer = tessy.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : PARTICLES)
                particle.renderParticle(buffer, player, partialTicks, x, xz, z, yz, xy);
            tessy.draw();

            GlStateManager.disableDepth();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : PARTICLES_NO_DEPTH)
                particle.renderParticle(buffer, player, partialTicks, x, xz, z, yz, xy);
            tessy.draw();
            GlStateManager.enableDepth();

            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);

            GlStateManager.popMatrix();
        }
    }

    public static int getParticleAmount(boolean depth) {
        return depth ? PARTICLES.size() : PARTICLES_NO_DEPTH.size();
    }

    public static void clearParticles() {
        if (!PARTICLES.isEmpty())
            PARTICLES.clear();
        if (!PARTICLES_NO_DEPTH.isEmpty())
            PARTICLES_NO_DEPTH.clear();
    }
}