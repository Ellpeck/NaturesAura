package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ITickableBlockEntity {

    void tick();

    static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected) {
        return expected == actual ? (l, p, s, e) -> ((ITickableBlockEntity) e).tick() : null;
    }
}
