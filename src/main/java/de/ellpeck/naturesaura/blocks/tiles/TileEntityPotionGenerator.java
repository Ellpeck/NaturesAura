package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
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

                List<AreaEffectCloudEntity> clouds = this.world.getEntitiesWithinAABB(AreaEffectCloudEntity.class, new AxisAlignedBB(this.pos).grow(2));
                for (AreaEffectCloudEntity cloud : clouds) {
                    if (cloud.isDead)
                        continue;

                    if (!addedOne) {
                        Potion type = ReflectionHelper.getPrivateValue(AreaEffectCloudEntity.class, cloud, "field_184502_e", "potion");
                        if (type == null)
                            continue;

                        for (EffectInstance effect : type.getEffects()) {
                            Effect potion = effect.getPotion();
                            if (potion.isBadEffect() || potion.isInstant()) {
                                continue;
                            }

                            int toAdd = ((effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25)) * 100;
                            boolean canGen = this.canGenerateRightNow(30, toAdd);
                            if (canGen)
                                while (toAdd > 0) {
                                    BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 30, this.pos);
                                    toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
                                }

                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(
                                    this.pos.getX(), this.pos.getY(), this.pos.getZ(), 5,
                                    PotionUtils.getPotionColor(type), canGen ? 1 : 0));

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

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
