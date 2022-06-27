package de.ellpeck.naturesaura.blocks.tiles;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.Map;

public class BlockEntityAuraTimer extends BlockEntityImpl implements ITickableBlockEntity {

    private static final Map<IAuraType, Integer> TIMES = ImmutableMap.<IAuraType, Integer>builder()
            .put(NaturesAuraAPI.TYPE_OVERWORLD, 20)
            .put(NaturesAuraAPI.TYPE_NETHER, 20 * 60)
            .put(NaturesAuraAPI.TYPE_END, 20 * 60 * 60).build();
    private final ItemStackHandlerNA itemHandler = new ItemStackHandlerNA(1, this, true) {
        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return stack.getItem() == ModItems.AURA_BOTTLE;
        }
    };
    private int timer;

    public BlockEntityAuraTimer(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AURA_TIMER, pos, state);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        if (this.redstonePower <= 0 && newPower > 0) {
            this.timer = 0;
            var color = ItemAuraBottle.getType(this.itemHandler.getStackInSlot(0)).getColor();
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.TIMER_RESET, color));
            this.sendToClients();
        }
        super.onRedstonePowerChange(newPower);
    }

    @Override
    public void tick() {
        var total = this.getTotalTime();
        if (total <= 0) {
            this.timer = 0;
            return;
        }

        if (this.level.isClientSide) {
            if (this.level.getGameTime() % 8 == 0) {
                var color = ItemAuraBottle.getType(this.itemHandler.getStackInSlot(0)).getColor();
                NaturesAuraAPI.instance().spawnMagicParticle(
                        this.worldPosition.getX() + 1 / 16F + this.level.random.nextFloat() * 14 / 16F,
                        this.worldPosition.getY() + 1 / 16F + this.level.random.nextFloat() * 14 / 16F,
                        this.worldPosition.getZ() + 1 / 16F + this.level.random.nextFloat() * 14 / 16F,
                        0, 0, 0, color, 1, 80 + this.level.random.nextInt(50), 0, false, true);
            }
            return;
        }

        this.timer++;
        if (this.timer >= total) {
            this.timer = 0;

            var state = this.getBlockState();
            this.level.setBlock(this.worldPosition, state.setValue(BlockStateProperties.POWERED, true), 1);
            this.level.scheduleTick(this.worldPosition, state.getBlock(), 4);

            var color = ItemAuraBottle.getType(this.itemHandler.getStackInSlot(0)).getColor();
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.TIMER_RESET, color));
        }
        if (this.timer % 2 == 0)
            this.sendToClients();
    }

    public int getTotalTime() {
        var stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty())
            return 0;
        var amount = BlockEntityAuraTimer.TIMES.get(ItemAuraBottle.getType(stack));
        if (amount == null)
            return 0;
        return amount * stack.getCount();
    }

    public int getTimeLeft() {
        return this.getTotalTime() - this.timer;
    }

    public float getTimerPercentage() {
        return this.timer / (float) this.getTotalTime();
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        return this.itemHandler;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.put("items", this.itemHandler.serializeNBT());
            compound.putInt("timer", this.timer);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.itemHandler.deserializeNBT(compound.getCompound("items"));
            this.timer = compound.getInt("timer");
        }
    }
}
