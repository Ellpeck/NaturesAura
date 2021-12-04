package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.WeatherType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.server.ServerLevel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class BlockEntityWeatherChanger extends BlockEntityImpl implements ITickableBlockEntity {

    private int processTime;
    private WeatherType type;
    private int itemAmount;

    public BlockEntityWeatherChanger() {
        super(ModTileEntities.WEATHER_CHANGER);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;
            if (this.processTime <= 0)
                return;
            int color = this.type == WeatherType.SUN ? 0xf5d742 : this.type == WeatherType.RAIN ? 0x4d5ae3 : 0x373247;
            for (int r = 0; r < 360; r += 20) {
                double xOff = Math.cos(Math.toRadians(r)) * 3F;
                double zOff = Math.sin(Math.toRadians(r)) * 3F;
                for (int i = this.level.rand.nextInt(3); i > 0; i--) {
                    NaturesAuraAPI.instance().spawnMagicParticle(
                            this.worldPosition.getX() + 0.5F + xOff,
                            this.worldPosition.getY(),
                            this.worldPosition.getZ() + 0.5F + zOff,
                            this.level.rand.nextGaussian() * 0.02F,
                            this.level.rand.nextFloat() * 0.1F + 0.1F,
                            this.level.rand.nextGaussian() * 0.02F,
                            color, this.level.rand.nextFloat() * 2 + 1, this.level.rand.nextInt(80) + 80, 0, false, true);
                }
            }
            return;
        }

        if (this.processTime > 0) {
            if (this.processTime % 20 == 0) {
                BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, 30000 * this.itemAmount);
            }

            this.processTime--;
            if (this.processTime <= 0) {
                this.sendToClients();
                int time = 6000 * this.itemAmount;
                ServerLevel server = (ServerLevel) this.level;
                switch (this.type) {
                    case SUN:
                        server.func_241113_a_(time, 0, false, false);
                        break;
                    case RAIN:
                        server.func_241113_a_(0, time, true, false);
                        break;
                    case THUNDERSTORM:
                        server.func_241113_a_(0, time, true, true);
                        break;
                }
            }
        } else {
            if (this.level.getGameTime() % 20 != 0)
                return;
            Pair<WeatherType, Integer> type = this.getNextWeatherType();
            if (type == null)
                return;
            this.type = type.getLeft();
            this.itemAmount = type.getRight();
            this.processTime = 100;
            this.sendToClients();
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.putInt("time", this.processTime);
            if (this.type != null)
                compound.putInt("weather", this.type.ordinal());
            compound.putInt("amount", this.itemAmount);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.processTime = compound.getInt("time");
            this.type = WeatherType.values()[compound.getInt("weather")];
            this.itemAmount = compound.getInt("amount");
        }
    }

    private Pair<WeatherType, Integer> getNextWeatherType() {
        AxisAlignedBB area = new AxisAlignedBB(this.worldPosition).grow(2);
        List<ItemEntity> items = this.level.getEntitiesWithinAABB(ItemEntity.class, area, EntityPredicates.IS_ALIVE);
        for (ItemEntity entity : items) {
            if (entity.cannotPickup())
                continue;
            ItemStack stack = entity.getItem();
            for (Map.Entry<ItemStack, WeatherType> entry : NaturesAuraAPI.WEATHER_CHANGER_CONVERSIONS.entrySet()) {
                if (!Helper.areItemsEqual(stack, entry.getKey(), true))
                    continue;
                entity.setItem(ItemStack.EMPTY);
                entity.remove();
                return Pair.of(entry.getValue(), stack.getCount());
            }
        }
        return null;
    }
}
