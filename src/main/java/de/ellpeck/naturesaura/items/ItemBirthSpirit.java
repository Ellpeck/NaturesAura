package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemBirthSpirit extends ItemGlowing {
    public ItemBirthSpirit() {
        super("birth_spirit");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBabyBorn(BabyEntitySpawnEvent event) {
        EntityLivingBase parent = event.getParentA();
        if (!parent.world.isRemote && event.getCausedByPlayer() != null) {
            BlockPos pos = parent.getPosition();
            int aura = IAuraChunk.getAuraInArea(parent.world, pos, 30);
            if (aura < 1200000)
                return;

            int amount = parent.world.rand.nextInt(3) + 1;
            EntityItem item = new EntityItem(parent.world, parent.posX, parent.posY, parent.posZ,
                    new ItemStack(ModItems.BIRTH_SPIRIT, amount));
            parent.world.spawnEntity(item);

            BlockPos spot = IAuraChunk.getHighestSpot(parent.world, pos, 30, pos);
            IAuraChunk.getAuraChunk(parent.world, spot).drainAura(spot, 800 * amount);
        }
    }
}
