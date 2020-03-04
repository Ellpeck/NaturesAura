package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AnimalEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "animal");

    private int chance;
    private AxisAlignedBB bb;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        int aura = IAuraChunk.getAuraInArea(world, pos, 30);
        if (aura < 1500000)
            return false;
        this.chance = Math.min(50, MathHelper.ceil(Math.abs(aura) / 500000F / IAuraChunk.getSpotAmountInArea(world, pos, 30)));
        if (this.chance <= 0)
            return false;
        int dist = MathHelper.clamp(Math.abs(aura) / 150000, 5, 35);
        this.bb = new AxisAlignedBB(pos).grow(dist);
        return true;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getPositionVector()))
            return ActiveType.INACTIVE;
        if (!NaturesAuraAPI.instance().isEffectPowderActive(player.world, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Items.EGG);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(world, pos, spot))
            return;

        List<AnimalEntity> animals = world.getEntitiesWithinAABB(AnimalEntity.class, this.bb);
        if (animals.size() >= 200)
            return;

        if (world.getGameTime() % 200 == 0) {
            List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, this.bb);
            for (ItemEntity item : items) {
                if (!item.isAlive())
                    continue;
                if (!NaturesAuraAPI.instance().isEffectPowderActive(world, item.getPosition(), NAME))
                    continue;

                ItemStack stack = item.getItem();
                if (!(stack.getItem() instanceof EggItem))
                    continue;
                // The getAge() method is client-side only for absolutely no reason but I want it so I don't care
                int age = ObfuscationReflectionHelper.getPrivateValue(ItemEntity.class, item, "field_70292_b");
                if (age < item.lifespan / 2)
                    continue;

                if (stack.getCount() <= 1)
                    item.remove();
                else {
                    stack.shrink(1);
                    item.setItem(stack);
                }

                ChickenEntity chicken = new ChickenEntity(EntityType.CHICKEN, world);
                chicken.setGrowingAge(-24000);
                chicken.setPosition(item.getPosX(), item.getPosY(), item.getPosZ());
                world.addEntity(chicken);

                BlockPos closestSpot = IAuraChunk.getHighestSpot(world, item.getPosition(), 35, pos);
                IAuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 2000);
            }
        }

        if (world.rand.nextInt(200) <= this.chance) {
            if (animals.size() < 2)
                return;
            AnimalEntity first = animals.get(world.rand.nextInt(animals.size()));
            if (first.isChild() || first.isInLove())
                return;
            if (!NaturesAuraAPI.instance().isEffectPowderActive(world, first.getPosition(), NAME))
                return;

            Optional<AnimalEntity> secondOptional = animals.stream()
                    .filter(e -> e != first && !e.isInLove() && !e.isChild())
                    .min(Comparator.comparingDouble(e -> e.getDistanceSq(first)));
            if (!secondOptional.isPresent())
                return;
            AnimalEntity second = secondOptional.get();
            if (second.getDistanceSq(first) > 5 * 5)
                return;

            this.setInLove(first);
            this.setInLove(second);

            BlockPos closestSpot = IAuraChunk.getHighestSpot(world, first.getPosition(), 35, pos);
            IAuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 3500);
        }
    }

    private void setInLove(AnimalEntity animal) {
        animal.setInLove(null);
        for (int j = 0; j < 7; j++)
            animal.world.addParticle(ParticleTypes.HEART,
                    animal.getPosX() + (double) (animal.world.rand.nextFloat() * animal.getWidth() * 2.0F) - animal.getWidth(),
                    animal.getPosY() + 0.5D + (double) (animal.world.rand.nextFloat() * animal.getHeight()),
                    animal.getPosZ() + (double) (animal.world.rand.nextFloat() * animal.getWidth() * 2.0F) - animal.getWidth(),
                    animal.world.rand.nextGaussian() * 0.02D,
                    animal.world.rand.nextGaussian() * 0.02D,
                    animal.world.rand.nextGaussian() * 0.02D);
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.animalEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
