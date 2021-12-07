package de.ellpeck.naturesaura.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record Matcher(BlockState defaultState, ICheck check) {

    public static Matcher wildcard() {
        return new Matcher(Blocks.AIR.defaultBlockState(), null);
    }

    public static Matcher tag(Block defaultBlock, Tag.Named<?> tag) {
        return new Matcher(defaultBlock.defaultBlockState(), (level, start, offset, pos, state, c) -> state.getBlock().getTags().contains(tag.getName()));
    }

    public interface ICheck {

        boolean matches(Level level, BlockPos start, BlockPos offset, BlockPos pos, BlockState state, char c);
    }
}
