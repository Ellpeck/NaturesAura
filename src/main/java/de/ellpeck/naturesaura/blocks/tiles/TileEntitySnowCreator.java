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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;

public class TileEntitySnowCreator extends TileEntityImpl implements ITickableTileEntity {

    private int snowmanCount;

    public TileEntitySnowCreator() {
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

        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 10 != 0)
                return;

            for (int i = 0; i < 10; i++) {
                double angle = this.world.rand.nextFloat() * Math.PI * 2;
                BlockPos pos = this.pos.add(Math.cos(angle) * range * this.world.rand.nextFloat(), 0, Math.sin(angle) * range * this.world.rand.nextFloat());
                pos = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
                BlockPos down = pos.down();

                Fluid fluid = this.world.getFluidState(down).getFluid();
                if (fluid == Fluids.WATER) {
                    this.world.setBlockState(down, Blocks.ICE.getDefaultState());
                } else if (Blocks.SNOW.getDefaultState().isValidPosition(this.world, pos) && this.world.getBlockState(pos).getBlock() != Blocks.SNOW && this.world.getBlockState(pos).getMaterial().isReplaceable()) {
                    this.world.setBlockState(pos, Blocks.SNOW.getDefaultState());

                    if (this.snowmanCount < range / 2 && this.world.rand.nextFloat() >= 0.995F) {
                        this.snowmanCount++;
                        Entity golem = new SnowGolemEntity(EntityType.SNOW_GOLEM, this.world);
                        golem.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                        this.world.addEntity(golem);
                    }
                } else {
                    continue;
                }

                BlockPos auraPos = IAuraChunk.getHighestSpot(this.world, this.pos, 30, this.pos);
                IAuraChunk.getAuraChunk(this.world, auraPos).drainAura(auraPos, 300);

                PacketHandler.sendToAllAround(this.world, this.pos, 32,
                        new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.SNOW_CREATOR));
                break;
            }
        } else {
            if (this.world.getGameTime() % 30 != 0)
                return;
            for (int i = range * 4; i >= 0; i--) {
                double angle = this.world.rand.nextFloat() * Math.PI * 2;
                BlockPos pos = this.pos.add(
                        Math.cos(angle) * range * this.world.rand.nextFloat(),
                        MathHelper.nextInt(this.world.rand, range / 2, range),
                        Math.sin(angle) * range * this.world.rand.nextFloat());
                NaturesAuraAPI.instance().spawnMagicParticle(
                        pos.getX() + this.world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + this.world.rand.nextFloat(),
                        this.world.rand.nextGaussian() * 0.05, 0, this.world.rand.nextGaussian() * 0.05,
                        0xdbe9ff, 1 + this.world.rand.nextFloat() * 1.5F, 10 * range, 0.05F + this.world.rand.nextFloat() * 0.05F, true, true
                );
            }
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE)
            compound.putInt("snowman_count", this.snowmanCount);
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE)
            this.snowmanCount = compound.getInt("snowman_count");
    }
}
