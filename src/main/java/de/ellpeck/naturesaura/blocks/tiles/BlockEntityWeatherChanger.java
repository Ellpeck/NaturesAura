package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.WeatherType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

public class BlockEntityWeatherChanger extends BlockEntityImpl implements ITickableBlockEntity {

    private int processTime;
    private WeatherType type;
    private int itemAmount;

    public BlockEntityWeatherChanger(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WEATHER_CHANGER, pos, state);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;
            if (this.processTime <= 0)
                return;
            var color = this.type == WeatherType.SUN ? 0xf5d742 : this.type == WeatherType.RAIN ? 0x4d5ae3 : 0x373247;
            for (var r = 0; r < 360; r += 20) {
                var xOff = Math.cos(Math.toRadians(r)) * 3F;
                var zOff = Math.sin(Math.toRadians(r)) * 3F;
                for (var i = this.level.random.nextInt(3); i > 0; i--) {
                    NaturesAuraAPI.instance().spawnMagicParticle(
                            this.worldPosition.getX() + 0.5F + xOff,
                            this.worldPosition.getY(),
                            this.worldPosition.getZ() + 0.5F + zOff,
                            this.level.random.nextGaussian() * 0.02F,
                            this.level.random.nextFloat() * 0.1F + 0.1F,
                            this.level.random.nextGaussian() * 0.02F,
                            color, this.level.random.nextFloat() * 2 + 1, this.level.random.nextInt(80) + 80, 0, false, true);
                }
            }
            return;
        }

        if (this.processTime > 0) {
            var auraToUse = 30000 * Mth.ceil(this.itemAmount * 0.75F);
            if (!this.canUseRightNow(auraToUse)) {
                this.processTime = 0;
                this.sendToClients();
                return;
            }

            if (this.processTime % 20 == 0) {
                var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, auraToUse);
            }

            this.processTime--;
            if (this.processTime <= 0) {
                this.sendToClients();
                var time = 6000 * this.itemAmount;
                var server = (ServerLevel) this.level;
                switch (this.type) {
                    case SUN -> server.setWeatherParameters(time, 0, false, false);
                    case RAIN -> server.setWeatherParameters(0, time, true, false);
                    case THUNDERSTORM -> server.setWeatherParameters(0, time, true, true);
                }
            }
        } else {
            if (this.level.getGameTime() % 20 != 0)
                return;
            var type = this.getNextWeatherType();
            if (type == null)
                return;
            this.type = type.getLeft();
            this.itemAmount = type.getRight();
            this.processTime = 100;
            this.sendToClients();
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        if (type != SaveType.BLOCK) {
            compound.putInt("time", this.processTime);
            if (this.type != null)
                compound.putInt("weather", this.type.ordinal());
            compound.putInt("amount", this.itemAmount);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type != SaveType.BLOCK) {
            this.processTime = compound.getInt("time");
            this.type = WeatherType.values()[compound.getInt("weather")];
            this.itemAmount = compound.getInt("amount");
        }
    }

    @Override
    public boolean allowsLowerLimiter() {
        return true;
    }

    private Pair<WeatherType, Integer> getNextWeatherType() {
        var area = new AABB(this.worldPosition).inflate(2);
        var items = this.level.getEntitiesOfClass(ItemEntity.class, area, Entity::isAlive);
        for (var entity : items) {
            if (entity.hasPickUpDelay())
                continue;
            var stack = entity.getItem();
            for (var entry : NaturesAuraAPI.WEATHER_CHANGER_CONVERSIONS.entrySet()) {
                if (!Helper.areItemsEqual(stack, entry.getKey(), true))
                    continue;
                entity.setItem(ItemStack.EMPTY);
                entity.kill();
                return Pair.of(entry.getValue(), stack.getCount());
            }
        }
        return null;
    }
}
