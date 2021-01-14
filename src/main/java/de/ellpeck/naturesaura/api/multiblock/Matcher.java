package de.ellpeck.naturesaura.api.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Matcher {

    private final BlockState defaultState;
    private final ICheck check;

    public Matcher(BlockState defaultState, ICheck check) {
        this.defaultState = defaultState;
        this.check = check;
    }

    public static Matcher wildcard() {
        return new Matcher(Blocks.AIR.getDefaultState(), null);
    }

    public static Matcher tag(Block defaultBlock, ITag.INamedTag tag) {
        return new Matcher(defaultBlock.getDefaultState(), (world, start, offset, pos, state, c) -> state.getBlock().getTags().contains(tag.getName()));
    }

    public BlockState getDefaultState() {
        return this.defaultState;
    }

    public ICheck getCheck() {
        return this.check;
    }

    public interface ICheck {
        boolean matches(World world, BlockPos start, BlockPos offset, BlockPos pos, BlockState state, char c);
    }
}
