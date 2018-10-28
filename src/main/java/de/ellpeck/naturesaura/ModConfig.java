package de.ellpeck.naturesaura;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(modid = NaturesAura.MOD_ID, category = "")
public final class ModConfig {

    public static General general = new General();
    public static Client client = new Client();

    public static class General {

        @Comment("If using Dragon's Breath in a Brewing Stand should not cause a glass bottle to appear")
        public boolean removeDragonBreathContainerItem = true;

    }

    public static class Client {

        @Comment("The percentage of particles that should be displayed, where 1 is 100% and 0 is 0%")
        @RangeDouble(min = 0, max = 1)
        public double particleAmount = 1;

        @Comment("If particle spawning should respect the particle setting in Minecraft's video settings screen")
        public boolean respectVanillaParticleSettings = true;
    }
}
