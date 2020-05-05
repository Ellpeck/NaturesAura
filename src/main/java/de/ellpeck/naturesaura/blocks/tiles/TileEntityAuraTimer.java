package de.ellpeck.naturesaura.blocks.tiles;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.Map;

public class TileEntityAuraTimer extends TileEntityImpl implements ITickableTileEntity {

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

    public TileEntityAuraTimer() {
        super(ModTileEntities.AURA_TIMER);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        if (this.redstonePower <= 0 && newPower > 0) {
            this.timer = 0;
            int color = ItemAuraBottle.getType(this.itemHandler.getStackInSlot(0)).getColor();
            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.TIMER_RESET, color));
            this.sendToClients();
        }
        super.onRedstonePowerChange(newPower);
    }

    @Override
    public void tick() {
        int total = this.getTotalTime();
        if (total <= 0) {
            this.timer = 0;
            return;
        }

        if (this.world.isRemote) {
            if (this.world.getGameTime() % 8 == 0) {
                int color = ItemAuraBottle.getType(this.itemHandler.getStackInSlot(0)).getColor();
                NaturesAuraAPI.instance().spawnMagicParticle(
                        this.pos.getX() + 1 / 16F + this.world.rand.nextFloat() * 14 / 16F,
                        this.pos.getY() + 1 / 16F + this.world.rand.nextFloat() * 14 / 16F,
                        this.pos.getZ() + 1 / 16F + this.world.rand.nextFloat() * 14 / 16F,
                        0, 0, 0, color, 1, 80 + this.world.rand.nextInt(50), 0, false, true);
            }
            return;
        }

        this.timer++;
        if (this.timer >= total) {
            this.timer = 0;

            BlockState state = this.getBlockState();
            this.world.setBlockState(this.pos, state.with(BlockStateProperties.POWERED, true), 1);
            this.world.getPendingBlockTicks().scheduleTick(this.pos, state.getBlock(), state.getBlock().tickRate(this.world));

            int color = ItemAuraBottle.getType(this.itemHandler.getStackInSlot(0)).getColor();
            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.TIMER_RESET, color));
        }
        if (this.timer % 2 == 0)
            this.sendToClients();
    }

    public int getTotalTime() {
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty())
            return 0;
        Integer amount = TIMES.get(ItemAuraBottle.getType(stack));
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
    public IItemHandlerModifiable getItemHandler(Direction facing) {
        return this.itemHandler;
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.put("items", this.itemHandler.serializeNBT());
            compound.putInt("timer", this.timer);
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.itemHandler.deserializeNBT(compound.getCompound("items"));
            this.timer = compound.getInt("timer");
        }
    }
}
