package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TileEntityChorusGenerator extends TileEntityImpl implements ITickableTileEntity {

    private final Deque<BlockPos> currentlyBreaking = new ArrayDeque<>();
    private int auraPerBlock;

    public TileEntityChorusGenerator() {
        super(ModTileEntities.CHORUS_GENERATOR);
    }

    @Override
    public void tick() {
        if (this.world.isRemote)
            return;
        if (this.world.getGameTime() % 5 != 0)
            return;
        if (this.currentlyBreaking.isEmpty())
            return;
        BlockPos pos = this.currentlyBreaking.removeLast();
        BlockState state = this.world.getBlockState(pos);
        if (state.getBlock() != Blocks.CHORUS_PLANT && state.getBlock() != Blocks.CHORUS_FLOWER) {
            this.currentlyBreaking.clear();
            return;
        }
        PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.CHORUS_GENERATOR, pos.getX(), pos.getY(), pos.getZ()));
        this.world.removeBlock(pos, false);
        this.world.playSound(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5,
                SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 0.5F, 1F);

        int aura = this.auraPerBlock;
        while (aura > 0) {
            BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 35, this.pos);
            aura -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, aura);
        }
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        if (this.redstonePower <= 0 && newPower > 0 && this.currentlyBreaking.isEmpty()) {
            int range = 2;
            xyz:
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = this.pos.add(x, y, z);
                        BlockState below = this.world.getBlockState(offset.down());
                        if (below.getBlock() != Blocks.END_STONE)
                            continue;
                        BlockState state = this.world.getBlockState(offset);
                        if (state.getBlock() != Blocks.CHORUS_PLANT)
                            continue;

                        List<BlockPos> plants = new ArrayList<>();
                        this.collectChorusPlant(offset, plants);
                        if (plants.size() <= 1)
                            continue;
                        this.currentlyBreaking.addAll(plants);
                        this.currentlyBreaking.addFirst(offset);

                        int aura = plants.size() * plants.size() * 300;
                        this.auraPerBlock = aura / plants.size();

                        break xyz;
                    }
                }
            }
        }
        super.onRedstonePowerChange(newPower);
    }

    private void collectChorusPlant(BlockPos pos, List<BlockPos> blocks) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN)
                continue;
            BlockPos offset = pos.offset(dir);
            if (blocks.contains(offset))
                continue;
            BlockState state = this.world.getBlockState(offset);
            if (state.getBlock() != Blocks.CHORUS_PLANT && state.getBlock() != Blocks.CHORUS_FLOWER)
                continue;
            blocks.add(offset);
            this.collectChorusPlant(offset, blocks);
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE) {
            ListNBT list = new ListNBT();
            for (BlockPos pos : this.currentlyBreaking)
                list.add(NBTUtil.writeBlockPos(pos));
            compound.put("breaking", list);
            compound.putInt("aura", this.auraPerBlock);
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE) {
            this.currentlyBreaking.clear();
            ListNBT list = compound.getList("breaking", 10);
            for (int i = 0; i < list.size(); i++)
                this.currentlyBreaking.add(NBTUtil.readBlockPos(list.getCompound(i)));
            this.auraPerBlock = compound.getInt("aura");
        }
    }
}
