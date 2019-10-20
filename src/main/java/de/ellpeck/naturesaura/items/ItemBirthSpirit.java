package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemBirthSpirit extends ItemGlowing {
    public ItemBirthSpirit() {
        super("birth_spirit");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBabyBorn(BabyEntitySpawnEvent event) {
        LivingEntity parent = event.getParentA();
        if (!parent.world.isRemote && event.getCausedByPlayer() != null) {
            BlockPos pos = parent.getPosition();
            int aura = IAuraChunk.getAuraInArea(parent.world, pos, 30);
            if (aura < 1200000)
                return;

            int amount = parent.world.rand.nextInt(3) + 1;
            ItemEntity item = new ItemEntity(parent.world, parent.posX, parent.posY, parent.posZ,
                    new ItemStack(ModItems.BIRTH_SPIRIT, amount));
            parent.world.spawnEntity(item);

            BlockPos spot = IAuraChunk.getHighestSpot(parent.world, pos, 30, pos);
            IAuraChunk.getAuraChunk(parent.world, spot).drainAura(spot, 800 * amount);
        }
    }
}
