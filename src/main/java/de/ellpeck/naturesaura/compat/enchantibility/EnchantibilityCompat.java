package de.ellpeck.naturesaura.compat.enchantibility;

import de.ellpeck.naturesaura.compat.ICompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import quarris.enchantability.api.EnchantabilityApi;
import quarris.enchantability.api.EnchantabilityApi.IInternals;

import java.util.Collections;

public class EnchantibilityCompat implements ICompat {
    @Override
    public void preInit() {
        IInternals api = EnchantabilityApi.getInstance();
        api.registerEnchantEffect(EnchantibilityAuraMending.RES, ModEnchantments.AURA_MENDING, EnchantibilityAuraMending::new);
        api.registerEffectComponent(EnchantibilityAuraMending.RES, PlayerTickEvent.class, EnchantibilityAuraMending::onPlayerTick, e -> Collections.singletonList(e.player));
    }

    @Override
    public void preInitClient() {

    }

    @Override
    public void postInit() {

    }

    @Override
    public void addItemTags(ItemTagProvider provider) {

    }
}
