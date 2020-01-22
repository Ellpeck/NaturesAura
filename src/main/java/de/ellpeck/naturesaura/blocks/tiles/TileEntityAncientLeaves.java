package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.NaturalAuraContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class TileEntityAncientLeaves extends TileEntityImpl {

    private final NaturalAuraContainer container = new NaturalAuraContainer(NaturesAuraAPI.TYPE_OVERWORLD, 2000, 500) {
        @Override
        public int getAuraColor() {
            return 0xCE5489;
        }

        @Override
        public int drainAura(int amountToDrain, boolean simulate) {
            int amount = super.drainAura(amountToDrain, simulate);
            if (amount > 0 && !simulate) {
                TileEntityAncientLeaves.this.sendToClients();
            }
            return amount;
        }
    };

    public TileEntityAncientLeaves() {
        super(ModTileEntities.ANCIENT_LEAVES);
    }

    @Override
    public IAuraContainer getAuraContainer(Direction facing) {
        return this.container;
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.container.writeNBT(compound);
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.container.readNBT(compound);
    }
}
