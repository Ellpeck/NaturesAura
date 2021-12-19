/*
package de.ellpeck.naturesaura.compat.enchantibility;

import de.ellpeck.naturesaura.compat.ICompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import quarris.enchantability.api.EnchantabilityApi;

import java.util.Collections;

public class EnchantibilityCompat implements ICompat {

    @Override
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            EnchantabilityApi.registerEnchantEffect(EnchantibilityAuraMending.RES, ModEnchantments.AURA_MENDING, EnchantibilityAuraMending::new);
            EnchantabilityApi.registerEffectComponent(EnchantibilityAuraMending.RES, TickEvent.PlayerTickEvent.class, EnchantibilityAuraMending::onPlayerTick, e -> Collections.singletonList(e.player));
        });
    }

    @Override
    public void setupClient() {

    }

    @Override
    public void addItemTags(ItemTagProvider provider) {

    }
}
*/
