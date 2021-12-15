package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AnimalEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "animal");

    private int chance;
    private AABB bb;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        var auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        this.chance = Math.min(50, Mth.ceil(Math.abs(aura) / 500000F / auraAndSpots.getRight()));
        if (this.chance <= 0)
            return false;
        var dist = Mth.clamp(Math.abs(aura) / 150000, 5, 35);
        this.bb = new AABB(pos).inflate(dist);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getEyePosition()))
            return ActiveType.INACTIVE;
        if (!NaturesAuraAPI.instance().isEffectPowderActive(player.level, player.blockPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Items.EGG);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 200 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;

        var animals = level.getEntitiesOfClass(Animal.class, this.bb);
        if (animals.size() >= ModConfig.instance.maxAnimalsAroundPowder.get())
            return;

        var items = level.getEntitiesOfClass(ItemEntity.class, this.bb);
        for (var item : items) {
            if (!item.isAlive())
                continue;
            if (!NaturesAuraAPI.instance().isEffectPowderActive(level, item.blockPosition(), NAME))
                continue;

            var stack = item.getItem();
            if (!(stack.getItem() instanceof EggItem))
                continue;
            if (item.getAge() < item.lifespan / 2)
                continue;

            if (stack.getCount() <= 1)
                item.kill();
            else {
                stack.shrink(1);
                item.setItem(stack);
            }

            var chicken = new Chicken(EntityType.CHICKEN, level);
            chicken.setAge(-24000);
            chicken.setPos(item.getX(), item.getY(), item.getZ());
            level.addFreshEntity(chicken);

            var closestSpot = IAuraChunk.getHighestSpot(level, item.blockPosition(), 35, pos);
            IAuraChunk.getAuraChunk(level, closestSpot).drainAura(closestSpot, 2000);
        }

        if (level.random.nextInt(20) <= this.chance) {
            if (animals.size() < 2)
                return;
            var first = animals.get(level.random.nextInt(animals.size()));
            if (first.isBaby() || first.isInLove())
                return;
            if (!NaturesAuraAPI.instance().isEffectPowderActive(level, first.blockPosition(), NAME))
                return;

            var secondOptional = animals.stream()
                    .filter(e -> e != first && !e.isInLove() && !e.isBaby())
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(first)));
            if (secondOptional.isEmpty())
                return;
            var second = secondOptional.get();
            if (second.distanceToSqr(first) > 5 * 5)
                return;

            this.setInLove(first);
            this.setInLove(second);

            var closestSpot = IAuraChunk.getHighestSpot(level, first.blockPosition(), 35, pos);
            IAuraChunk.getAuraChunk(level, closestSpot).drainAura(closestSpot, 3500);
        }
    }

    private void setInLove(Animal animal) {
        animal.setInLove(null);
        for (var j = 0; j < 7; j++)
            animal.level.addParticle(ParticleTypes.HEART,
                    animal.getX() + (double) (animal.level.random.nextFloat() * animal.getBbWidth() * 2.0F) - animal.getBbWidth(),
                    animal.getY() + 0.5D + (double) (animal.level.random.nextFloat() * animal.getBbHeight()),
                    animal.getZ() + (double) (animal.level.random.nextFloat() * animal.getBbWidth() * 2.0F) - animal.getBbWidth(),
                    animal.level.random.nextGaussian() * 0.02D,
                    animal.level.random.nextGaussian() * 0.02D,
                    animal.level.random.nextGaussian() * 0.02D);
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.animalEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
