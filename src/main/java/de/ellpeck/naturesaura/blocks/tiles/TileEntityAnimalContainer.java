package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileEntityAnimalContainer extends TileEntityImpl implements ITickableTileEntity {

    public TileEntityAnimalContainer() {
        super(ModTileEntities.ANIMAL_CONTAINER);
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
        if (this.world.isRemote || this.world.getGameTime() % 2 != 0)
            return;

        int radius = this.getRadius();
        Set<AnimalEntity> animalsInRange = new HashSet<>(this.world.getEntitiesWithinAABB(AnimalEntity.class, new AxisAlignedBB(this.pos).grow(radius - 1)));
        List<AnimalEntity> animalsOutRange = this.world.getEntitiesWithinAABB(AnimalEntity.class, new AxisAlignedBB(this.pos).grow(radius));
        for (AnimalEntity animal : animalsOutRange) {
            if (animalsInRange.contains(animal))
                continue;
            Vec3d pos = animal.getPositionVec();
            Vec3d distance = pos.subtract(this.pos.getX(), pos.getY(), this.pos.getZ());
            distance = distance.normalize().scale(-0.15F);
            animal.setMotion(distance);

            if (this.world.rand.nextBoolean()) {
                Vec3d eye = animal.getEyePosition(1).add(animal.getLookVec());
                PacketHandler.sendToAllAround(this.world, this.pos, 32,
                        new PacketParticles((float) eye.getX(), (float) eye.getY(), (float) eye.getZ(), PacketParticles.Type.ANIMAL_CONTAINER));
            }
        }
    }
}
