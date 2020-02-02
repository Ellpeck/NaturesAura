package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.Heightmap;

public class TileEntitySnowCreator extends TileEntityImpl implements ITickableTileEntity {
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

            BlockPos pos = this.pos.add(MathHelper.nextInt(this.world.rand, -range, range), 0, MathHelper.nextInt(this.world.rand, -range, range));
            pos = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
            BlockPos down = pos.down();

            Fluid fluid = this.world.getFluidState(down).getFluid();
            if (fluid == Fluids.WATER) {
                this.world.setBlockState(down, Blocks.ICE.getDefaultState());
            } else if (Blocks.SNOW.getDefaultState().isValidPosition(this.world, pos)) {
                this.world.setBlockState(pos, Blocks.SNOW.getDefaultState());
            } else {
                return;
            }

            BlockPos auraPos = IAuraChunk.getHighestSpot(this.world, this.pos, 30, this.pos);
            IAuraChunk.getAuraChunk(this.world, auraPos).drainAura(auraPos, 300);
        } else {
            if (this.world.getGameTime() % 30 != 0)
                return;
            for (int i = range * 5; i >= 0; i--) {
                BlockPos randomPos = this.pos.add(
                        MathHelper.nextInt(this.world.rand, -range, range),
                        MathHelper.nextInt(this.world.rand, range / 2, range),
                        MathHelper.nextInt(this.world.rand, -range, range));
                NaturesAuraAPI.instance().spawnMagicParticle(
                        randomPos.getX() + this.world.rand.nextFloat(), randomPos.getY() + 1, randomPos.getZ() + this.world.rand.nextFloat(),
                        this.world.rand.nextGaussian() * 0.05, 0, this.world.rand.nextGaussian() * 0.05,
                        0xdbe9ff, 1 + this.world.rand.nextFloat() * 1.5F, 10 * range, 0.05F + this.world.rand.nextFloat() * 0.05F, true, true
                );
            }
        }
    }
}
