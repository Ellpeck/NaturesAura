package de.ellpeck.naturesaura.particles;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleMagic extends Particle {

    public static final ResourceLocation TEXTURE = new ResourceLocation(NaturesAura.MOD_ID, "textures/particles/magic_round.png");

    private final float desiredScale;
    private final boolean fade;
    private float particleScale;

    public ParticleMagic(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        super(world, posX, posY, posZ);
        this.desiredScale = scale;
        this.maxAge = maxAge;
        this.canCollide = collision;
        this.particleGravity = gravity;
        this.fade = fade;

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        float r = (((color >> 16) & 255) / 255F) * (1F - this.rand.nextFloat() * 0.25F);
        float g = (((color >> 8) & 255) / 255F) * (1F - this.rand.nextFloat() * 0.25F);
        float b = ((color & 255) / 255F) * (1F - this.rand.nextFloat() * 0.25F);
        this.setColor(r, g, b);

        this.particleAlpha = 1F;
        this.particleScale = 0F;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.age++;
        if (this.age > this.maxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.04D * (double) this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);

            float lifeRatio = (float) this.age / (float) this.maxAge;
            if (this.fade && lifeRatio > 0.75F)
                this.particleAlpha = 1F - (lifeRatio - 0.75F) / 0.25F;
            if (lifeRatio <= 0.25F)
                this.particleScale = this.desiredScale * (lifeRatio / 0.25F);
            else if (this.fade)
                this.particleScale = this.desiredScale * (1F - (lifeRatio - 0.25F) / 0.75F);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        double x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
        double y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
        double z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;
        float sc = 0.1F * this.particleScale;

        int brightness = this.getBrightnessForRender(partialTicks);
        int sky = brightness >> 16 & 0xFFFF;
        int block = brightness & 0xFFFF;

        buffer.pos(x + (-rotationX * sc - rotationXY * sc), y + -rotationZ * sc, z + (-rotationYZ * sc - rotationXZ * sc))
                .tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(sky, block).endVertex();
        buffer.pos(x + (-rotationX * sc + rotationXY * sc), y + (rotationZ * sc), z + (-rotationYZ * sc + rotationXZ * sc))
                .tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(sky, block).endVertex();
        buffer.pos(x + (rotationX * sc + rotationXY * sc), y + (rotationZ * sc), z + (rotationYZ * sc + rotationXZ * sc))
                .tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(sky, block).endVertex();
        buffer.pos(x + (rotationX * sc - rotationXY * sc), y + (-rotationZ * sc), z + (rotationYZ * sc - rotationXZ * sc))
                .tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(sky, block).endVertex();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15 << 20 | 15 << 4;
    }
}