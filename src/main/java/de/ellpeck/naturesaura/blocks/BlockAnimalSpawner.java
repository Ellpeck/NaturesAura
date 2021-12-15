package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalSpawner;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BlockAnimalSpawner extends BlockContainerImpl {

    public BlockAnimalSpawner() {
        super("animal_spawner", BlockEntityAnimalSpawner::new, Properties.of(Material.STONE).strength(2F).sound(SoundType.STONE));
    }
}
