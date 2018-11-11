package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.AuraType;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityFlowerGenerator extends TileEntityImpl implements ITickable {

    private static final List<IBlockState> FLOWERS = new ArrayList<>();

    static {
        for (Block block : ForgeRegistries.BLOCKS) {
            if (block instanceof BlockFlower) {
                FLOWERS.addAll(block.getBlockState().getValidStates());
            }
        }
    }

    private final Map<IBlockState, MutableInt> consumedRecently = new HashMap<>();

    @Override
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 10 == 0) {
            List<BlockPos> possible = new ArrayList<>();
            int range = 3;
            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos offset = this.pos.add(x, 0, z);
                    IBlockState state = this.world.getBlockState(offset);
                    if (FLOWERS.contains(state)) {
                        possible.add(offset);
                    }
                }
            }

            if (possible.isEmpty())
                return;

            BlockPos pos = possible.get(this.world.rand.nextInt(possible.size()));
            IBlockState state = this.world.getBlockState(pos);
            MutableInt curr = this.consumedRecently.computeIfAbsent(state, s -> new MutableInt());

            int addAmount = 100;
            int toAdd = Math.max(0, addAmount - curr.getValue());
            if (toAdd > 0) {
                BlockPos auraPos = IAuraChunk.getLowestSpot(this.world, this.pos, 30, this.pos);
                if (AuraType.OVERWORLD.isPresent(this.world) && IAuraChunk.getAuraInArea(this.world, auraPos, 30) < 20000)
                    IAuraChunk.getAuraChunk(this.world, auraPos).storeAura(auraPos, toAdd);
                else
                    toAdd = 0;
            }

            for (Map.Entry<IBlockState, MutableInt> entry : this.consumedRecently.entrySet()) {
                if (entry.getKey() != state) {
                    MutableInt val = entry.getValue();
                    if (val.getValue() > 0)
                        val.subtract(1);
                }
            }
            curr.add(5);

            this.world.setBlockToAir(pos);

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
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);

        if (type != SaveType.SYNC && !this.consumedRecently.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<IBlockState, MutableInt> entry : this.consumedRecently.entrySet()) {
                IBlockState state = entry.getKey();
                Block block = state.getBlock();

                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("block", block.getRegistryName().toString());
                tag.setInteger("meta", block.getMetaFromState(state));
                tag.setInteger("amount", entry.getValue().intValue());
                list.appendTag(tag);
            }
            compound.setTag("consumed_recently", list);
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);

        if (type != SaveType.SYNC) {
            this.consumedRecently.clear();

            NBTTagList list = compound.getTagList("consumed_recently", 10);
            for (NBTBase base : list) {
                NBTTagCompound tag = (NBTTagCompound) base;

                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("block")));
                if (block != null) {
                    IBlockState state = block.getStateFromMeta(tag.getInteger("meta"));
                    this.consumedRecently.put(state, new MutableInt(tag.getInteger("amount")));
                }
            }

        }
    }
}
