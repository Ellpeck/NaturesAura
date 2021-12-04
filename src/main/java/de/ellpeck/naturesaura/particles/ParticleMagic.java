package de.ellpeck.naturesaura.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.level.ClientLevel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class ParticleMagic extends Particle {

    public static final ResourceLocation TEXTURE = new ResourceLocation(NaturesAura.MOD_ID, "textures/particles/magic_round.png");

    private final float desiredScale;
    private final boolean fade;
    private final boolean depth;
    private float particleScale;

    public ParticleMagic(ClientLevel level, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade, boolean depth) {
        super(level, posX, posY, posZ);
        this.desiredScale = scale;
        this.maxAge = maxAge;
        this.canCollide = collision;
        this.particleGravity = gravity;
        this.fade = fade;
        this.depth = depth;

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
    public void move(double x, double y, double z) {
        double lastY = y;
        if (this.canCollide && (x != 0 || y != 0 || z != 0)) {
            Vector3d motion = Entity.collideBoundingBoxHeuristically(null, new Vector3d(x, y, z), this.getBoundingBox(), this.level, ISelectionContext.dummy(), new ReuseableStream<>(Stream.empty()));
            x = motion.x;
            y = motion.y;
            z = motion.z;
        }
        if (x != 0 || y != 0 || z != 0) {
            this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        }
        this.onGround = lastY != y && lastY < 0;
        if (this.onGround) {
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d vec3d = renderInfo.getProjectedView();
        float f = (float) (Mth.lerp(partialTicks, this.prevPosX, this.posX) - vec3d.getX());
        float f1 = (float) (Mth.lerp(partialTicks, this.prevPosY, this.posY) - vec3d.getY());
        float f2 = (float) (Mth.lerp(partialTicks, this.prevPosZ, this.posZ) - vec3d.getZ());
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
        return this.depth ? ParticleHandler.MAGIC : ParticleHandler.MAGIC_NO_DEPTH;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15 << 20 | 15 << 4;
    }
}