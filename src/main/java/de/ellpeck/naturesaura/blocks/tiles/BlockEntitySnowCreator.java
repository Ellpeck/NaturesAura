package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;

public class BlockEntitySnowCreator extends BlockEntityImpl implements ITickableBlockEntity {

    private int snowmanCount;

    public BlockEntitySnowCreator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SNOW_CREATOR, pos, state);
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
        var range = this.getRange();
        if (range <= 0)
            return;

        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;

            for (var i = 0; i < 10; i++) {
                var angle = this.level.random.nextFloat() * Math.PI * 2;
                var pos = this.worldPosition.offset(Math.cos(angle) * range * this.level.random.nextFloat(), 0, Math.sin(angle) * range * this.level.random.nextFloat());
                pos = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
                var down = pos.below();

                var fluid = this.level.getFluidState(down).getType();
                if (fluid == Fluids.WATER) {
                    if (this.level.getBlockState(down).getMaterial().isReplaceable())
                        this.level.setBlockAndUpdate(down, Blocks.ICE.defaultBlockState());
                } else if (Blocks.SNOW.defaultBlockState().canSurvive(this.level, pos) && this.level.getBlockState(pos).getBlock() != Blocks.SNOW && this.level.getBlockState(pos).getMaterial().isReplaceable()) {
                    this.level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());

                    if (this.snowmanCount < range / 2 && this.level.random.nextFloat() >= 0.995F) {
                        this.snowmanCount++;
                        Entity golem = new SnowGolem(EntityType.SNOW_GOLEM, this.level);
                        golem.setPos(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                        this.level.addFreshEntity(golem);
                    }
                } else {
                    continue;
                }

                var auraPos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 30, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, auraPos).drainAura(auraPos, 300);

                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.SNOW_CREATOR));
                break;
            }
        } else {
            if (this.level.getGameTime() % 30 != 0)
                return;
            for (var i = range * 4; i >= 0; i--) {
                var angle = this.level.random.nextFloat() * Math.PI * 2;
                var pos = this.worldPosition.offset(
                        Math.cos(angle) * range * this.level.random.nextFloat(),
                        Mth.nextInt(this.level.random, range / 2, range),
                        Math.sin(angle) * range * this.level.random.nextFloat());
                NaturesAuraAPI.instance().spawnMagicParticle(
                        pos.getX() + this.level.random.nextFloat(), pos.getY() + 1, pos.getZ() + this.level.random.nextFloat(),
                        this.level.random.nextGaussian() * 0.05, 0, this.level.random.nextGaussian() * 0.05,
                        0xdbe9ff, 1 + this.level.random.nextFloat() * 1.5F, 10 * range, 0.05F + this.level.random.nextFloat() * 0.05F, true, true
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
