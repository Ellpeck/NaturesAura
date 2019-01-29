package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

public class TileEntityPotionGenerator extends TileEntityImpl implements ITickable {

    @Override
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 10 == 0) {
            if (Multiblocks.POTION_GENERATOR.isComplete(this.world, this.pos)) {
                boolean addedOne = false;

                List<EntityAreaEffectCloud> clouds = this.world.getEntitiesWithinAABB(EntityAreaEffectCloud.class, new AxisAlignedBB(this.pos).grow(2));
                for (EntityAreaEffectCloud cloud : clouds) {
                    if (cloud.isDead)
                        continue;

                    if (!addedOne) {
                        PotionType type = ReflectionHelper.getPrivateValue(EntityAreaEffectCloud.class, cloud, "field_184502_e", "potion");
                        if (type == null)
                            continue;

                        for (PotionEffect effect : type.getEffects()) {
                            Potion potion = effect.getPotion();
                            if (potion.isBadEffect() || potion.isInstant()) {
                                continue;
                            }

                            int toAdd = ((effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25)) * 100;
                            while (toAdd > 0) {
                                BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 30, this.pos);
                                toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
                            }

                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(
                                    this.pos.getX(), this.pos.getY(), this.pos.getZ(), 5,
                                    PotionUtils.getPotionColor(type)));

                            addedOne = true;
                            break;
                        }
                    }

                    float newRadius = cloud.getRadius() - 0.25F;
                    if (newRadius < 0.5F)
                        cloud.setDead();
                    else
                        cloud.setRadius(newRadius);
                }
            }
        }
    }
}
