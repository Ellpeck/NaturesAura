package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Set;

public class BlockEntityAnimalContainer extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityAnimalContainer(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANIMAL_CONTAINER, pos, state);
    }

    public int getRadius() {
        return this.redstonePower / 2;
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        this.sendToClients();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;
        var radius = this.getRadius();
        Set<Animal> animalsInRange = new HashSet<>(this.level.getEntitiesOfClass(Animal.class, new AABB(this.worldPosition).inflate(radius - 1)));
        var animalsOutRange = this.level.getEntitiesOfClass(Animal.class, new AABB(this.worldPosition).inflate(radius + 1));
        for (var animal : animalsOutRange) {
            if (animalsInRange.contains(animal))
                continue;
            var pos = animal.position();
            var distance = pos.subtract(this.worldPosition.getX(), pos.y, this.worldPosition.getZ());
            distance = distance.normalize().scale(-0.15F);
            animal.setDeltaMovement(distance);

            if (this.level.random.nextBoolean()) {
                var eye = animal.getEyePosition(1).add(animal.getLookAngle());
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles((float) eye.x, (float) eye.y, (float) eye.z, PacketParticles.Type.ANIMAL_CONTAINER));
            }
        }
    }
}
