package de.ellpeck.naturesaura.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.Locale;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class ParticleHandler {

    public static final IParticleRenderType MAGIC = new IParticleRenderType() {
        @Override
        public void beginRender(BufferBuilder buffer, TextureManager textureManager) {
            setupRendering(textureManager);
            RenderSystem.enableDepthTest();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
        }

        @Override
        public String toString() {
            return NaturesAura.MOD_ID.toUpperCase(Locale.ROOT) + "_MAGIC";
        }
    };

    public static final IParticleRenderType MAGIC_NO_DEPTH = new IParticleRenderType() {
        @Override
        public void beginRender(BufferBuilder buffer, TextureManager textureManager) {
            setupRendering(textureManager);
            RenderSystem.disableDepthTest();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
        }

        @Override
        public String toString() {
            return NaturesAura.MOD_ID.toUpperCase(Locale.ROOT) + "_MAGIC_NO_DEPTH";
        }
    };

    public static boolean depthEnabled = true;
    public static int range = 32;
    public static boolean culling = true;

    public static void spawnParticle(Supplier<Particle> particle, double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.getDistanceSq(x, y, z) <= range * range) {
            if (culling) {
                if (ModConfig.instance.respectVanillaParticleSettings.get()) {
                    ParticleStatus setting = mc.gameSettings.particles;
                    if (setting != ParticleStatus.ALL &&
                            (setting != ParticleStatus.DECREASED || mc.world.rand.nextInt(3) != 0) &&
                            (setting != ParticleStatus.MINIMAL || mc.world.rand.nextInt(10) != 0))
                        return;
                }
                double setting = ModConfig.instance.particleAmount.get();
                if (setting < 1 && mc.world.rand.nextDouble() > setting)
                    return;
            }
            mc.particles.addEffect(particle.get());
        }
    }

    private static void setupRendering(TextureManager textureManager) {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.alphaFunc(516, 0.003921569F);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.enableFog();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(false);
        textureManager.bindTexture(ParticleMagic.TEXTURE);
    }
}