package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockEntityFlowerGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private final Map<BlockState, MutableInt> consumedRecently = new HashMap<>();

    public BlockEntityFlowerGenerator(BlockPos pos, BlockState state) {
        super(ModTileEntities.FLOWER_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            List<BlockPos> possible = new ArrayList<>();
            int range = 3;
            for (int x = -range; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = this.worldPosition.offset(x, y, z);
                        BlockState state = this.level.getBlockState(offset);
                        if (BlockTags.SMALL_FLOWERS.contains(state.getBlock()))
                            possible.add(offset);
                    }
                }
            }

            if (possible.isEmpty())
                return;

            BlockPos pos = possible.get(this.level.random.nextInt(possible.size()));
            BlockState state = this.level.getBlockState(pos);
            MutableInt curr = this.consumedRecently.computeIfAbsent(state, s -> new MutableInt());

            int addAmount = 25000;
            int toAdd = Math.max(0, addAmount - curr.getValue() * 100);
            if (toAdd > 0) {
                if (IAuraType.forLevel(this.level).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) && this.canGenerateRightNow(toAdd)) {
                    this.generateAura(toAdd);
                } else {
                    toAdd = 0;
                }
            }

            for (Map.Entry<BlockState, MutableInt> entry : this.consumedRecently.entrySet()) {
                if (entry.getKey() != state) {
                    MutableInt val = entry.getValue();
                    if (val.getValue() > 0)
                        val.subtract(1);
                }
            }
            curr.add(5);

            this.level.removeBlock(pos, false);

            int color = Helper.blendColors(0x5ccc30, 0xe53c16, toAdd / (float) addAmount);
            if (toAdd > 0) {
                for (int i = this.level.random.nextInt(5) + 5; i >= 0; i--)
                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticleStream(
                            pos.getX() + 0.25F + this.level.random.nextFloat() * 0.5F,
                            pos.getY() + 0.25F + this.level.random.nextFloat() * 0.5F,
                            pos.getZ() + 0.25F + this.level.random.nextFloat() * 0.5F,
                            this.worldPosition.getX() + 0.25F + this.level.random.nextFloat() * 0.5F,
                            this.worldPosition.getY() + 0.25F + this.level.random.nextFloat() * 0.5F,
                            this.worldPosition.getZ() + 0.25F + this.level.random.nextFloat() * 0.5F,
                            this.level.random.nextFloat() * 0.02F + 0.1F, color, 1F
                    ));
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.FLOWER_GEN_AURA_CREATION));
            }
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), PacketParticles.Type.FLOWER_GEN_CONSUME, color));
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);

        if (type != SaveType.SYNC && !this.consumedRecently.isEmpty()) {
            ListTag list = new ListTag();
            for (Map.Entry<BlockState, MutableInt> entry : this.consumedRecently.entrySet()) {
                BlockState state = entry.getKey();
                Block block = state.getBlock();

                CompoundTag tag = new CompoundTag();
                tag.putString("block", block.getRegistryName().toString());
                tag.putInt("amount", entry.getValue().intValue());
                list.add(tag);
            }
            compound.put("consumed_recently", list);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.SYNC) {
            this.consumedRecently.clear();
            ListTag list = compound.getList("consumed_recently", 10);
            for (Tag base : list) {
                CompoundTag tag = (CompoundTag) base;
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("block")));
                if (block != null)
                    this.consumedRecently.put(block.defaultBlockState(), new MutableInt(tag.getInt("amount")));
            }

        }
    }
}
