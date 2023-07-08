package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalSpawner;
import net.minecraft.world.level.block.SoundType;

public class BlockAnimalSpawner extends BlockContainerImpl {

    public BlockAnimalSpawner() {
        super("animal_spawner", BlockEntityAnimalSpawner.class, Properties.of().strength(2F).sound(SoundType.STONE));
    }
}
