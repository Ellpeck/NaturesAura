package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityFlowerGenerator extends TileEntityImpl implements ITickableTileEntity {

    private final Map<BlockState, MutableInt> consumedRecently = new HashMap<>();

    public TileEntityFlowerGenerator() {
        super(ModTileEntities.FLOWER_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.world.getGameTime() % 10 == 0) {
            List<BlockPos> possible = new ArrayList<>();
            int range = 3;
            for (int x = -range; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = this.pos.add(x, y, z);
                        BlockState state = this.world.getBlockState(offset);
                        if (NaturesAuraAPI.FLOWERS.contains(state)) {
                            possible.add(offset);
                        }
                    }
                }
            }

            if (possible.isEmpty())
                return;

            BlockPos pos = possible.get(this.world.rand.nextInt(possible.size()));
            BlockState state = this.world.getBlockState(pos);
            MutableInt curr = this.consumedRecently.computeIfAbsent(state, s -> new MutableInt());

            int addAmount = 25000;
            int toAdd = Math.max(0, addAmount - curr.getValue() * 100);
            if (toAdd > 0) {
                if (IAuraType.forWorld(this.world).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) && this.canGenerateRightNow(30, toAdd)) {
                    int remain = toAdd;
                    while (remain > 0) {
                        BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 30, this.pos);
                        remain -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, remain);
                    }
                } else
                    toAdd = 0;
            }

            for (Map.Entry<BlockState, MutableInt> entry : this.consumedRecently.entrySet()) {
                if (entry.getKey() != state) {
                    MutableInt val = entry.getValue();
                    if (val.getValue() > 0)
                        val.subtract(1);
                }
            }
            curr.add(5);

            this.world.removeBlock(pos, false);

            int color = Helper.blendColors(0x5ccc30, 0xe53c16, toAdd / (float) addAmount);
            if (toAdd > 0) {
                for (int i = this.world.rand.nextInt(5) + 5; i >= 0; i--)
                    PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                            pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                            pos.getY() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                            pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                            this.pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                            this.pos.getY() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                            this.pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                            this.world.rand.nextFloat() * 0.02F + 0.1F, color, 1F
                    ));
                PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 8));
            }
            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), 7, color));
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);

        if (type != SaveType.SYNC && !this.consumedRecently.isEmpty()) {
            ListNBT list = new ListNBT();
            for (Map.Entry<BlockState, MutableInt> entry : this.consumedRecently.entrySet()) {
                BlockState state = entry.getKey();
                Block block = state.getBlock();

                CompoundNBT tag = new CompoundNBT();
                tag.putString("block", block.getRegistryName().toString());
                tag.putInt("amount", entry.getValue().intValue());
                list.add(tag);
            }
            compound.put("consumed_recently", list);
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.SYNC) {
            this.consumedRecently.clear();
            ListNBT list = compound.getList("consumed_recently", 10);
            for (INBT base : list) {
                CompoundNBT tag = (CompoundNBT) base;
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("block")));
                if (block != null)
                    this.consumedRecently.put(block.getDefaultState(), new MutableInt(tag.getInt("amount")));
            }

        }
    }
}
