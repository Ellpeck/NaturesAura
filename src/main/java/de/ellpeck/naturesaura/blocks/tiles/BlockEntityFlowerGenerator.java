package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockEntityFlowerGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private final Map<BlockState, MutableInt> consumedRecently = new HashMap<>();

    public BlockEntityFlowerGenerator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLOWER_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            List<BlockPos> possible = new ArrayList<>();
            var range = 3;
            for (var x = -range; x <= range; x++) {
                for (var y = -1; y <= 1; y++) {
                    for (var z = -range; z <= range; z++) {
                        var offset = this.worldPosition.offset(x, y, z);
                        var state = this.level.getBlockState(offset);
                        if (state.is(BlockTags.SMALL_FLOWERS))
                            possible.add(offset);
                    }
                }
            }

            if (possible.isEmpty())
                return;

            var pos = possible.get(this.level.random.nextInt(possible.size()));
            var state = this.level.getBlockState(pos);
            var curr = this.consumedRecently.computeIfAbsent(state, s -> new MutableInt());

            var addAmount = 25000;
            var toAdd = Math.max(0, addAmount - curr.getValue() * 100);
            if (toAdd > 0) {
                if (IAuraType.forLevel(this.level).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) && this.canGenerateRightNow(toAdd)) {
                    this.generateAura(toAdd);
                } else {
                    toAdd = 0;
                }
            }

            for (var entry : this.consumedRecently.entrySet()) {
                if (entry.getKey() != state) {
                    var val = entry.getValue();
                    if (val.getValue() > 0)
                        val.subtract(1);
                }
            }
            curr.add(5);

            this.level.removeBlock(pos, false);

            var color = Helper.blendColors(0x5ccc30, 0xe53c16, toAdd / (float) addAmount);
            if (toAdd > 0) {
                for (var i = this.level.random.nextInt(5) + 5; i >= 0; i--)
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
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);

        if (type != SaveType.SYNC && !this.consumedRecently.isEmpty()) {
            var list = new ListTag();
            for (var entry : this.consumedRecently.entrySet()) {
                var state = entry.getKey();
                var block = state.getBlock();

                var tag = new CompoundTag();
                tag.putString("block", BuiltInRegistries.BLOCK.getKey(block).toString());
                tag.putInt("amount", entry.getValue().intValue());
                list.add(tag);
            }
            compound.put("consumed_recently", list);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type != SaveType.SYNC) {
            this.consumedRecently.clear();
            var list = compound.getList("consumed_recently", 10);
            for (var base : list) {
                var tag = (CompoundTag) base;
                var block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("block")));
                if (block != null)
                    this.consumedRecently.put(block.defaultBlockState(), new MutableInt(tag.getInt("amount")));
            }

        }
    }

}
