package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AnimalEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "animal");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return;
        int aura = IAuraChunk.getAuraInArea(world, pos, 30);
        if (aura < 15000)
            return;
        int chance = Math.min(50, Math.abs(aura) / 5000);
        if (chance <= 0)
            return;
        int dist = MathHelper.clamp(Math.abs(aura) / 1500, 5, 35);
        AxisAlignedBB bb = new AxisAlignedBB(pos).grow(dist);

        List<EntityAnimal> animals = world.getEntitiesWithinAABB(EntityAnimal.class, bb);
        if (animals.size() >= 200)
            return;

        if (world.getTotalWorldTime() % 200 == 0) {
            List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, bb);
            for (EntityItem item : items) {
                if (item.isDead)
                    continue;
                if (NaturesAuraAPI.instance().isEffectPowderActive(world, item.getPosition(), NAME))
                    continue;

                ItemStack stack = item.getItem();
                if (!(stack.getItem() instanceof ItemEgg))
                    continue;
                // The getAge() method is private for absolutely no reason but I want it so I don't care
                int age = ReflectionHelper.getPrivateValue(EntityItem.class, item, "field_70292_b", "age");
                if (age < item.lifespan / 2)
                    continue;

                if (stack.getCount() <= 1)
                    item.setDead();
                else {
                    stack.shrink(1);
                    item.setItem(stack);
                }

                EntityChicken chicken = new EntityChicken(world);
                chicken.setGrowingAge(-24000);
                chicken.setPosition(item.posX, item.posY, item.posZ);
                world.spawnEntity(chicken);

                BlockPos closestSpot = IAuraChunk.getHighestSpot(world, item.getPosition(), 35, pos);
                IAuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 150);
            }
        }

        if (world.rand.nextInt(200) <= chance) {
            if (animals.size() < 2)
                return;
            EntityAnimal first = animals.get(world.rand.nextInt(animals.size()));
            if (first.isChild() || first.isInLove())
                return;
            if (NaturesAuraAPI.instance().isEffectPowderActive(world, first.getPosition(), NAME))
                return;

            Optional<EntityAnimal> secondOptional = animals.stream()
                    .filter(e -> e != first && !e.isInLove() && !e.isChild())
                    .min(Comparator.comparingDouble(e -> e.getDistanceSq(first)));
            if (!secondOptional.isPresent())
                return;
            EntityAnimal second = secondOptional.get();
            if (second.getDistanceSq(first) > 5 * 5)
                return;

            this.setInLove(first);
            this.setInLove(second);

            BlockPos closestSpot = IAuraChunk.getHighestSpot(world, first.getPosition(), 35, pos);
            IAuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 200);
        }
    }

    private void setInLove(EntityAnimal animal) {
        animal.setInLove(null);
        for (int j = 0; j < 7; j++)
            animal.world.spawnParticle(EnumParticleTypes.HEART,
                    (animal.posX + (double) (animal.world.rand.nextFloat() * animal.width * 2.0F)) - animal.width,
                    animal.posY + 0.5D + (double) (animal.world.rand.nextFloat() * animal.height),
                    (animal.posZ + (double) (animal.world.rand.nextFloat() * animal.width * 2.0F)) - animal.width,
                    animal.world.rand.nextGaussian() * 0.02D,
                    animal.world.rand.nextGaussian() * 0.02D,
                    animal.world.rand.nextGaussian() * 0.02D);
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.animalEffect;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
