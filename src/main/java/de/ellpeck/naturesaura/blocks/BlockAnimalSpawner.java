package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalSpawner;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockAnimalSpawner extends BlockContainerImpl {
    public BlockAnimalSpawner() {
        super("animal_spawner", BlockEntityAnimalSpawner::new, Properties.create(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }
}
