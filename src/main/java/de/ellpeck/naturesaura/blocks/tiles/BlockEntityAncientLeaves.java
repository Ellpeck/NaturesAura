package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.NaturalAuraContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityAncientLeaves extends BlockEntityImpl {

    public final NaturalAuraContainer container = new NaturalAuraContainer(NaturesAuraAPI.TYPE_OVERWORLD, 2000, 500) {
        @Override
        public int getAuraColor() {
            return 0xCE5489;
        }

        @Override
        public int drainAura(int amountToDrain, boolean simulate) {
            var amount = super.drainAura(amountToDrain, simulate);
            if (amount > 0 && !simulate) {
                BlockEntityAncientLeaves.this.sendToClients();
            }
            return amount;
        }
    };

    public BlockEntityAncientLeaves(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_LEAVES, pos, state);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.container.writeNBT(compound);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.container.readNBT(compound);
    }
}
