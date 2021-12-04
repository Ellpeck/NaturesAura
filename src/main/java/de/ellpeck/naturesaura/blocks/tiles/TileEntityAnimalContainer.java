package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.passive.Animal;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockEntityAnimalContainer extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityAnimalContainer(BlockPos pos, BlockState state) {
        super(ModTileEntities.ANIMAL_CONTAINER, pos, state);
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
        int radius = this.getRadius();
        Set<Animal> animalsInRange = new HashSet<>(this.level.getEntitiesWithinAABB(Animal.class, new AxisAlignedBB(this.worldPosition).grow(radius - 1)));
        List<Animal> animalsOutRange = this.level.getEntitiesWithinAABB(Animal.class, new AxisAlignedBB(this.worldPosition).grow(radius + 1));
        for (Animal animal : animalsOutRange) {
            if (animalsInRange.contains(animal))
                continue;
            Vec3 pos = animal.position();
            Vec3 distance = pos.subtract(this.worldPosition.getX(), pos.y, this.worldPosition.getZ());
            distance = distance.normalize().scale(-0.15F);
            animal.setMotion(distance);

            if (this.level.rand.nextBoolean()) {
                Vec3 eye = animal.getEyePosition(1).add(animal.getLookAngle());
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles((float) eye.x, (float) eye.y, (float) eye.z, PacketParticles.Type.ANIMAL_CONTAINER));
            }
        }
    }
}
