package de.ellpeck.naturesaura.api.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Matcher {

    private final BlockState defaultState;
    private final ICheck check;

    public Matcher(BlockState defaultState, ICheck check) {
        this.defaultState = defaultState;
        this.check = check;
    }

    public BlockState getDefaultState() {
        return this.defaultState;
    }

    public ICheck getCheck() {
        return this.check;
    }

    public static Matcher wildcard() {
        return new Matcher(Blocks.AIR.getDefaultState(), null);
    }

    public static Matcher oreDict(Block defaultBlock, String name) {
        return new Matcher(defaultBlock.getDefaultState(),
                (world, start, offset, pos, state, otherC) -> state.getBlock() == defaultBlock);
        /* TODO return new Matcher(defaultBlock.getDefaultState(), new ICheck() {
            private List<BlockState> states;

            @Override
            public boolean matches(World world, BlockPos start, BlockPos offset, BlockPos pos, BlockState state, char c) {
                if (this.states == null) {
                    this.states = new ArrayList<>();
                    for (ItemStack stack : OreDictionary.getOres(name)) {
                        Block block = Block.getBlockFromItem(stack.getItem());
                        if (block != null && block != Blocks.AIR) {
                            int damage = stack.getItemDamage();
                            if (damage == OreDictionary.WILDCARD_VALUE)
                                this.states.addAll(block.getBlockState().getValidStates());
                            else
                                this.states.add(block.getStateFromMeta(damage));
                        }
                    }
                }

                return this.states.isEmpty() || this.states.contains(state);
            }
        });*/
    }

    public interface ICheck {
        boolean matches(World world, BlockPos start, BlockPos offset, BlockPos pos, BlockState state, char c);
    }
}
