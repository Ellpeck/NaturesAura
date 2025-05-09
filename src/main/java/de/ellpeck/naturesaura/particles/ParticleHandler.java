package de.ellpeck.naturesaura.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class ParticleHandler {

    public static final ParticleRenderType MAGIC = new ParticleRenderType() {

        @Override
        public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            ParticleHandler.setupRendering();
            RenderSystem.enableDepthTest();
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return NaturesAura.MOD_ID.toUpperCase(Locale.ROOT) + "_MAGIC";
        }
    };

    public static final ParticleRenderType MAGIC_NO_DEPTH = new ParticleRenderType() {
        @Override
        public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            ParticleHandler.setupRendering();
            RenderSystem.disableDepthTest();
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
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
        var mc = Minecraft.getInstance();
        if (mc.player.distanceToSqr(x, y, z) <= ParticleHandler.range * ParticleHandler.range) {
            if (ParticleHandler.culling) {
                if (ModConfig.instance.respectVanillaParticleSettings.get()) {
                    var setting = mc.options.particles().get();
                    if (setting != ParticleStatus.ALL &&
                        (setting != ParticleStatus.DECREASED || mc.level.random.nextInt(3) != 0) &&
                        (setting != ParticleStatus.MINIMAL || mc.level.random.nextInt(10) != 0))
                        return;
                }
                double setting = ModConfig.instance.particleAmount.get();
                if (setting < 1 && mc.level.random.nextDouble() > setting)
                    return;
            }
            mc.particleEngine.add(particle.get());
        }
    }

    private static void setupRendering() {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ParticleMagic.TEXTURE);
    }

}
