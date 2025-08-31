package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class BlockEntityPotionGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private static final Field POTION_CONTENTS_FIELD = ObfuscationReflectionHelper.findField(AreaEffectCloud.class, "potionContents");

    public BlockEntityPotionGenerator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POTION_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            if (Multiblocks.POTION_GENERATOR.isComplete(this.level, this.worldPosition)) {
                var addedOne = false;

                var clouds = this.level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(this.worldPosition).inflate(2));
                for (var cloud : clouds) {
                    if (!cloud.isAlive())
                        continue;

                    if (!addedOne) {
                        PotionContents type;
                        try {
                            type = (PotionContents) BlockEntityPotionGenerator.POTION_CONTENTS_FIELD.get(cloud);
                        } catch (IllegalAccessException e) {
                            NaturesAura.LOGGER.fatal("Couldn't reflect furnace field", e);
                            continue;
                        }

                        for (var effect : type.getAllEffects()) {
                            var potion = effect.getEffect().value();
                            if (!potion.isBeneficial() || potion.isInstantenous()) {
                                continue;
                            }

                            var toAdd = (effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25) * 100;
                            var canGen = this.canGenerateRightNow(toAdd);
                            if (canGen) {
                                this.generateAura(toAdd);
                            } else {
                                this.spawnCannotGenerateParticles();
                            }

                            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(
                                this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.POTION_GEN,
                                type.getColor(), canGen ? 1 : 0));

                            addedOne = true;
                            break;
                        }
                    }

                    var newRadius = cloud.getRadius() - 0.25F;
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
