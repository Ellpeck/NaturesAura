package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;

public final class DrainSpotEffects {

    public static void init() {
        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(GrassDieEffect.NAME, GrassDieEffect::new);
        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(PlantBoostEffect.NAME, PlantBoostEffect::new);
        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(ReplenishingEffect.NAME, ReplenishingEffect::new);
        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(BalanceEffect.NAME, BalanceEffect::new);
        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(ExplosionEffect.NAME, ExplosionEffect::new);
    }
}
