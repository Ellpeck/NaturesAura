/* TODO Enchantability
package de.ellpeck.naturesaura.compat.enchantibility;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import quarris.enchantability.api.enchants.AbstractEnchantEffect;

public class EnchantibilityAuraMending extends AbstractEnchantEffect {

    public static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "aura_mending");

    public EnchantibilityAuraMending(PlayerEntity player, Enchantment enchantment, int level) {
        super(player, enchantment, level);
    }

    public static void onPlayerTick(EnchantibilityAuraMending enchant, PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (event.player.world.isRemote || event.player.world.getGameTime() % 10 != 0)
            return;
        if (!event.player.isShiftKeyDown() || event.player.getHealth() >= event.player.getMaxHealth())
            return;
        int usage = 5000;
        if (NaturesAuraAPI.instance().extractAuraFromPlayer(event.player, usage, false))
            event.player.heal(1);
    }

    @Override
    public ResourceLocation getName() {
        return RES;
    }
}
*/
