package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;
import net.minecraft.level.gen.Heightmap;

public class BlockEntitySnowCreator extends BlockEntityImpl implements ITickableBlockEntity {

    private int snowmanCount;

    public BlockEntitySnowCreator() {
        super(ModTileEntities.SNOW_CREATOR);
    }

    public int getRange() {
        return this.redstonePower * 2;
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        this.sendToClients();
    }

    @Override
    public void tick() {
        int range = this.getRange();
        if (range <= 0)
            return;

        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;

            for (int i = 0; i < 10; i++) {
                double angle = this.level.rand.nextFloat() * Math.PI * 2;
                BlockPos pos = this.worldPosition.add(Math.cos(angle) * range * this.level.rand.nextFloat(), 0, Math.sin(angle) * range * this.level.rand.nextFloat());
                pos = this.level.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
                BlockPos down = pos.down();

                Fluid fluid = this.level.getFluidState(down).getFluid();
                if (fluid == Fluids.WATER) {
                    if (this.level.getBlockState(down).getMaterial().isReplaceable())
                        this.level.setBlockState(down, Blocks.ICE.getDefaultState());
                } else if (Blocks.SNOW.getDefaultState().isValidPosition(this.level, pos) && this.level.getBlockState(pos).getBlock() != Blocks.SNOW && this.level.getBlockState(pos).getMaterial().isReplaceable()) {
                    this.level.setBlockState(pos, Blocks.SNOW.getDefaultState());

                    if (this.snowmanCount < range / 2 && this.level.rand.nextFloat() >= 0.995F) {
                        this.snowmanCount++;
                        Entity golem = new SnowGolemEntity(EntityType.SNOW_GOLEM, this.level);
                        golem.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                        this.level.addEntity(golem);
                    }
                } else {
                    continue;
                }

                BlockPos auraPos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 30, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, auraPos).drainAura(auraPos, 300);

                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.SNOW_CREATOR));
                break;
            }
        } else {
            if (this.level.getGameTime() % 30 != 0)
                return;
            for (int i = range * 4; i >= 0; i--) {
                double angle = this.level.rand.nextFloat() * Math.PI * 2;
                BlockPos pos = this.worldPosition.add(
                        Math.cos(angle) * range * this.level.rand.nextFloat(),
                        Mth.nextInt(this.level.rand, range / 2, range),
                        Math.sin(angle) * range * this.level.rand.nextFloat());
                NaturesAuraAPI.instance().spawnMagicParticle(
                        pos.getX() + this.level.rand.nextFloat(), pos.getY() + 1, pos.getZ() + this.level.rand.nextFloat(),
                        this.level.rand.nextGaussian() * 0.05, 0, this.level.rand.nextGaussian() * 0.05,
                        0xdbe9ff, 1 + this.level.rand.nextFloat() * 1.5F, 10 * range, 0.05F + this.level.rand.nextFloat() * 0.05F, true, true
                );
            }
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE)
            compound.putInt("snowman_count", this.snowmanCount);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE)
            this.snowmanCount = compound.getInt("snowman_count");
    }
}
