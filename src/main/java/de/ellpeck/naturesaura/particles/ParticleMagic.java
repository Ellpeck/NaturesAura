package de.ellpeck.naturesaura.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

        float r = (color >> 16 & 255) / 255F * (1F - this.rand.nextFloat() * 0.25F);
        float g = (color >> 8 & 255) / 255F * (1F - this.rand.nextFloat() * 0.25F);
        float b = (color & 255) / 255F * (1F - this.rand.nextFloat() * 0.25F);
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
            if (Math.abs(this.posY - this.prevPosY) <= 0.01F) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }

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
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Vec3d vec3d = renderInfo.getProjectedView();
        float f = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - vec3d.getX());
        float f1 = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - vec3d.getY());
        float f2 = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - vec3d.getZ());
        Quaternion quaternion = renderInfo.getRotation();
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = 0.1F * this.particleScale;

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        int j = this.getBrightnessForRender(partialTicks);
        buffer.pos(avector3f[0].getX(), avector3f[0].getY(), avector3f[0].getZ()).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer.pos(avector3f[1].getX(), avector3f[1].getY(), avector3f[1].getZ()).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer.pos(avector3f[2].getX(), avector3f[2].getY(), avector3f[2].getZ()).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer.pos(avector3f[3].getX(), avector3f[3].getY(), avector3f[3].getZ()).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
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