package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BlockEntityPotionGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityPotionGenerator(BlockPos pos, BlockState state) {
        super(ModTileEntities.POTION_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            if (Multiblocks.POTION_GENERATOR.isComplete(this.level, this.worldPosition)) {
                boolean addedOne = false;

                List<AreaEffectCloud> clouds = this.level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(this.worldPosition).inflate(2));
                for (AreaEffectCloud cloud : clouds) {
                    if (!cloud.isAlive())
                        continue;

                    if (!addedOne) {
                        Potion type = cloud.getPotion();
                        if (type == null)
                            continue;

                        for (MobEffectInstance effect : type.getEffects()) {
                            MobEffect potion = effect.getEffect();
                            if (!potion.isBeneficial() || potion.isInstantenous()) {
                                continue;
                            }

                            int toAdd = (effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25) * 100;
                            boolean canGen = this.canGenerateRightNow(toAdd);
                            if (canGen)
                                this.generateAura(toAdd);

                            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(
                                    this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.POTION_GEN,
                                    PotionUtils.getColor(type), canGen ? 1 : 0));

                            addedOne = true;
                            break;
                        }
                    }

                    float newRadius = cloud.getRadius() - 0.25F;
                    if (newRadius < 0.5F) {
                        cloud.kill();
                    } else {
                        cloud.setRadius(newRadius);
                    }
                }
            }
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
