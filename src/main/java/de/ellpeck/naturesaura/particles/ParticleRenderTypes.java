package de.ellpeck.naturesaura.particles;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ParticleRenderTypes {
    /**
     * @see IParticleRenderType#PARTICLE_SHEET_TRANSLUCENT
     */
    public static final IParticleRenderType PARTICLE_MAGIC_TRANSLUCENT = new IParticleRenderType() {
        public void beginRender(BufferBuilder buffer, TextureManager textureManager) {
            setupTranslucent();

            RenderSystem.enableDepthTest();
            textureManager.bindTexture(ParticleMagic.TEXTURE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "PARTICLE_MAGIC_TRANSLUCENT";
        }
    };
    /**
     * Same as PARTICLE_MAGIC_TRANSLUCENT but without depth test
     */
    public static final IParticleRenderType PARTICLE_MAGIC_TRANSLUCENT_NO_DEPTH = new IParticleRenderType() {
        public void beginRender(BufferBuilder buffer, TextureManager textureManager) {
            setupTranslucent();

            RenderSystem.disableDepthTest();
            textureManager.bindTexture(ParticleMagic.TEXTURE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "PARTICLE_MAGIC_TRANSLUCENT_NO_DEPTH";
        }
    };

    public static void setupTranslucent() {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.003921569F);
        RenderSystem.disableCull();
        RenderSystem.enableFog();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
