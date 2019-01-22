package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.entities.render.RenderEffectInhibitor;
import de.ellpeck.naturesaura.entities.render.RenderMoverMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class ModEntities {

    public static void init() {
        EntityRegistry.registerModEntity(
                new ResourceLocation(NaturesAura.MOD_ID, "effect_inhibitor"),
                EntityEffectInhibitor.class, NaturesAura.MOD_ID + ".effect_inhibitor",
                0, NaturesAura.MOD_ID, 64, 20, false);
        NaturesAura.proxy.registerEntityRenderer(EntityEffectInhibitor.class, () -> RenderEffectInhibitor::new);

        EntityRegistry.registerModEntity(
                new ResourceLocation(NaturesAura.MOD_ID, "mover_cart"),
                EntityMoverMinecart.class, NaturesAura.MOD_ID + ".mover_cart",
                1, NaturesAura.MOD_ID, 64, 3, true);
        NaturesAura.proxy.registerEntityRenderer(EntityMoverMinecart.class, () -> RenderMoverMinecart::new);
    }
}
