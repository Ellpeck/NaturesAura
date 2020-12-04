package de.ellpeck.naturesaura.particles;

import de.ellpeck.naturesaura.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class ParticleHandler {

    private static final List<Particle> PARTICLES = new ArrayList<>();
    private static final List<Particle> PARTICLES_NO_DEPTH = new ArrayList<>();
    public static boolean depthEnabled = true;
    public static int range = 32;
    public static boolean culling = true;

    public static void spawnParticle(Supplier<Particle> particleSupplier, double x, double y, double z) {
        if (Minecraft.getInstance().player.getDistanceSq(x, y, z) <= range * range) {
            Minecraft mc = Minecraft.getInstance();
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
            Particle particle = particleSupplier.get();
            if (depthEnabled)
                PARTICLES.add(particle);
            else
                PARTICLES_NO_DEPTH.add(particle);
            mc.particles.addEffect(particle);
        }
    }

    public static void updateParticles() {
        updateList(PARTICLES);
        updateList(PARTICLES_NO_DEPTH);

        depthEnabled = true;
        range = 32;
        culling = true;
    }

    private static void updateList(List<Particle> particles) {
        //particles.forEach(Particle::tick); // No longer needed because using vanilla particle system
        particles.removeIf((particle) -> !particle.isAlive());
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