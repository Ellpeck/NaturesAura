package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BlockEntityChorusGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private final Deque<BlockPos> currentlyBreaking = new ArrayDeque<>();
    private int auraPerBlock;

    public BlockEntityChorusGenerator() {
        super(ModTileEntities.CHORUS_GENERATOR);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;
        if (this.level.getGameTime() % 5 != 0)
            return;
        if (this.currentlyBreaking.isEmpty())
            return;
        BlockPos pos = this.currentlyBreaking.removeLast();
        BlockState state = this.level.getBlockState(pos);
        if (state.getBlock() != Blocks.CHORUS_PLANT && state.getBlock() != Blocks.CHORUS_FLOWER) {
            this.currentlyBreaking.clear();
            return;
        }
        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.CHORUS_GENERATOR, pos.getX(), pos.getY(), pos.getZ()));
        this.level.removeBlock(pos, false);
        this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5,
                SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 0.5F, 1F);
        this.generateAura(this.auraPerBlock);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        if (this.redstonePower <= 0 && newPower > 0 && this.currentlyBreaking.isEmpty()) {
            int range = 2;
            xyz:
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = this.worldPosition.add(x, y, z);
                        BlockState below = this.level.getBlockState(offset.down());
                        if (below.getBlock() != Blocks.END_STONE)
                            continue;
                        BlockState state = this.level.getBlockState(offset);
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
            BlockState state = this.level.getBlockState(offset);
            if (state.getBlock() != Blocks.CHORUS_PLANT && state.getBlock() != Blocks.CHORUS_FLOWER)
                continue;
            blocks.add(offset);
            this.collectChorusPlant(offset, blocks);
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
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
    public void readNBT(CompoundTag compound, SaveType type) {
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
