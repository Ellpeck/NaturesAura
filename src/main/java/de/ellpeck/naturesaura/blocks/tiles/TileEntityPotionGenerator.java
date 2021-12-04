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
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;

public class BlockEntityPotionGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityPotionGenerator() {
        super(ModTileEntities.POTION_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            if (Multiblocks.POTION_GENERATOR.isComplete(this.level, this.worldPosition)) {
                boolean addedOne = false;

                List<AreaEffectCloudEntity> clouds = this.level.getEntitiesWithinAABB(AreaEffectCloudEntity.class, new AxisAlignedBB(this.worldPosition).grow(2));
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

                            int toAdd = (effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25) * 100;
                            boolean canGen = this.canGenerateRightNow(toAdd);
                            if (canGen)
                                this.generateAura(toAdd);

                            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(
                                    this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.POTION_GEN,
                                    PotionUtils.getPotionColor(type), canGen ? 1 : 0));

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
