package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.BasicAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlockEntityEndFlower extends BlockEntityImpl implements ITickableBlockEntity {

    private final BasicAuraContainer container = new BasicAuraContainer(null, 500000) {
        {
            this.aura = this.maxAura;
        }

        @Override
        public int storeAura(int amountToStore, boolean simulate) {
            return 0;
        }

        @Override
        public int drainAura(int amountToDrain, boolean simulate) {
            int amount = super.drainAura(amountToDrain, simulate);
            if (amount > 0 && !simulate)
                BlockEntityEndFlower.this.sendToClients();
            return amount;
        }

        @Override
        public int getAuraColor() {
            return 0x6a25dd;
        }
    };

    public boolean isDrainMode;

    public BlockEntityEndFlower() {
        super(ModTileEntities.END_FLOWER);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;

            if (!this.isDrainMode) {
                List<ItemEntity> items = this.level.getEntitiesWithinAABB(ItemEntity.class,
                        new AxisAlignedBB(this.worldPosition).grow(1), EntityPredicates.IS_ALIVE);
                for (ItemEntity item : items) {
                    if (item.cannotPickup())
                        continue;
                    ItemStack stack = item.getItem();
                    if (stack.getCount() != 1)
                        continue;
                    if (stack.getItem() != Items.ENDER_EYE)
                        continue;

                    this.isDrainMode = true;
                    item.remove();

                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                            new PacketParticles((float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.END_FLOWER_CONSUME, this.container.getAuraColor()));
                    break;
                }
            } else {
                int toDrain = Math.min(5000, this.container.getStoredAura());
                this.container.drainAura(toDrain, false);
                this.generateAura(toDrain);

                if (this.container.getStoredAura() <= 0) {
                    this.level.setBlockState(this.worldPosition, Blocks.DEAD_BUSH.getDefaultState());
                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                            new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.END_FLOWER_DECAY, this.container.getAuraColor()));
                }
            }
        } else {
            if (this.isDrainMode && this.level.getGameTime() % 5 == 0)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        this.worldPosition.getX() + 0.25F + this.level.rand.nextFloat() * 0.5F,
                        this.worldPosition.getY() + 0.25F + this.level.rand.nextFloat() * 0.5F,
                        this.worldPosition.getZ() + 0.25F + this.level.rand.nextFloat() * 0.5F,
                        this.level.rand.nextGaussian() * 0.05F,
                        this.level.rand.nextFloat() * 0.1F,
                        this.level.rand.nextGaussian() * 0.05F,
                        this.container.getAuraColor(), this.level.rand.nextFloat() * 2F + 1F, 50, 0F, false, true);
        }
    }

    @Override
    public IAuraContainer getAuraContainer() {
        return this.container;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.container.writeNBT(compound);
            compound.putBoolean("drain_mode", this.isDrainMode);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.container.readNBT(compound);
            this.isDrainMode = compound.getBoolean("drain_mode");
        }
    }
}
