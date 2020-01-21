package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;

public class TileEntityPotionGenerator extends TileEntityImpl implements ITickableTileEntity {

    public TileEntityPotionGenerator(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.world.getGameTime() % 10 == 0) {
            if (Multiblocks.POTION_GENERATOR.isComplete(this.world, this.pos)) {
                boolean addedOne = false;

                List<AreaEffectCloudEntity> clouds = this.world.getEntitiesWithinAABB(AreaEffectCloudEntity.class, new AxisAlignedBB(this.pos).grow(2));
                for (AreaEffectCloudEntity cloud : clouds) {
                    if (!cloud.isAlive())
                        continue;

                    if (!addedOne) {
                        Potion type = ObfuscationReflectionHelper.getPrivateValue(AreaEffectCloudEntity.class, cloud, "field_184502_e");
                        if (type == null)
                            continue;

                        for (EffectInstance effect : type.getEffects()) {
                            Effect potion = effect.getPotion();
                            if (!potion.isBeneficial() || potion.isInstant()) {
                                continue;
                            }

                            int toAdd = ((effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25)) * 100;
                            boolean canGen = this.canGenerateRightNow(30, toAdd);
                            if (canGen)
                                while (toAdd > 0) {
                                    BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 30, this.pos);
                                    toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
                                }

                            // TODO particles
                           /* PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(
                                    this.pos.getX(), this.pos.getY(), this.pos.getZ(), 5,
                                    PotionUtils.getPotionColor(type), canGen ? 1 : 0));*/

                            addedOne = true;
                            break;
                        }
                    }

                    float newRadius = cloud.getRadius() - 0.25F;
                    if (newRadius < 0.5F)
                        cloud.remove();
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
