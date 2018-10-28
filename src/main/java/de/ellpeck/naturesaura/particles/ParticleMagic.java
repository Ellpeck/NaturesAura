package de.ellpeck.naturesaura.particles;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleMagic extends Particle {

    public static final ResourceLocation TEXTURE = new ResourceLocation(NaturesAura.MOD_ID, "particles/magic_round");

    private final float desiredScale;
    private final boolean fade;

    public ParticleMagic(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        super(world, posX, posY, posZ);
        this.desiredScale = scale;
        this.particleMaxAge = maxAge;
        this.canCollide = collision;
        this.particleGravity = gravity;
        this.fade = fade;

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        float r = (((color >> 16) & 255) / 255F) * (1F - this.rand.nextFloat() * 0.35F);
        float g = (((color >> 8) & 255) / 255F) * (1F - this.rand.nextFloat() * 0.35F);
        float b = ((color & 255) / 255F) * (1F - this.rand.nextFloat() * 0.35F);
        this.setRBGColorF(r, g, b);

        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        this.setParticleTexture(map.getAtlasSprite(TEXTURE.toString()));

        this.particleAlpha = 0.75F;
        this.particleScale = this.desiredScale;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.particleAge++;
        if (this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.04D * (double) this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.fade) {
                float lifeRatio = (float) this.particleAge / (float) this.particleMaxAge;
                this.particleAlpha = 0.75F - (lifeRatio * 0.75F);
                this.particleScale = this.desiredScale - (this.desiredScale * lifeRatio);
            }
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15 << 20 | 15 << 4;
    }
}