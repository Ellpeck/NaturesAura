package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockAnimalSpawner extends BlockContainerImpl {
    public BlockAnimalSpawner() {
        super("animal_spawner", ModTileEntities.ANIMAL_SPAWNER, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }
}
