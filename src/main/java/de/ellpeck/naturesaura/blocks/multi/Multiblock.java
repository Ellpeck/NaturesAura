package de.ellpeck.naturesaura.blocks.multi;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Multiblock {

    public static final Map<ResourceLocation, Multiblock> MULTIBLOCKS = new HashMap<>();

    public final ResourceLocation name;
    public final Map<BlockPos, Matcher> matchers = new HashMap<>();
    public final int width;
    public final int height;
    public final int depth;
    public final int xOffset;
    public final int yOffset;
    public final int zOffset;
    public final char[][][] rawPattern;

    public Multiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        this.name = name;

        int width = -1;
        this.height = pattern.length;
        int depth = -1;
        int xOff = 0;
        int yOff = 0;
        int zOff = 0;
        char[][][] raw = null;

        for (int i = 0; i < pattern.length; i++) {
            String[] row = pattern[i];

            if (width < 0)
                width = row.length;
            else if (row.length != width)
                throw new IllegalArgumentException();

            for (int j = 0; j < row.length; j++) {
                String column = row[j];
                if (depth < 0)
                    depth = column.length();
                else if (column.length() != depth)
                    throw new IllegalArgumentException();

                if (raw == null)
                    raw = new char[width][this.height][depth];
                for (int k = 0; k < column.length(); k++) {
                    char c = column.charAt(k);
                    raw[k][this.height - 1 - i][j] = c;

                    if (c == '0') {
                        xOff = k;
                        yOff = this.height - 1 - i;
                        zOff = j;
                    }
                }
            }
        }
        this.depth = depth;
        this.width = width;
        this.xOffset = xOff;
        this.yOffset = yOff;
        this.zOffset = zOff;
        this.rawPattern = raw;

        Map<Character, Matcher> matchers = new HashMap<>();
        for (int i = 0; i < rawMatchers.length; i += 2) {
            char c = (char) rawMatchers[i];
            if (matchers.containsKey(c))
                continue;

            Object value = rawMatchers[i + 1];
            if (value instanceof IBlockState)
                matchers.put(c, Matcher.state((IBlockState) value));
            else if (value instanceof Block)
                matchers.put(c, Matcher.block((Block) value));
            else
                matchers.put(c, (Matcher) value);
        }

        for (int x = 0; x < this.width; x++)
            for (int y = 0; y < this.height; y++)
                for (int z = 0; z < this.depth; z++) {
                    Matcher matcher = matchers.get(this.rawPattern[x][y][z]);
                    if (matcher == null)
                        throw new IllegalStateException();
                    if (!matcher.isWildcard)
                        this.matchers.put(new BlockPos(x, y, z), matcher);
                }

        for (int i = 1; i < rawMatchers.length; i += 2) {
            if (rawMatchers[i] instanceof Matcher) {
                Matcher matcher = (Matcher) rawMatchers[i];
                if (matcher.isWildcard)
                    rawMatchers[i] = PatchouliAPI.instance.anyMatcher();
                else
                    rawMatchers[i] = PatchouliAPI.instance.predicateMatcher(matcher.defaultState,
                            state -> matcher.check.matches(null, null, null, null, state, (char) 0));
            }
        }
        PatchouliAPI.instance.registerMultiblock(name, PatchouliAPI.instance.makeMultiblock(pattern, rawMatchers));

        MULTIBLOCKS.put(this.name, this);
    }

    public boolean isComplete(World world, BlockPos center) {
        BlockPos start = this.getStart(center);
        return this.forEach(center, (char) 0, (pos, matcher) -> {
            BlockPos offset = pos.subtract(start);
            return matcher.check.matches(world, start, offset, pos, world.getBlockState(pos), this.getChar(offset));
        });
    }

    public boolean forEach(BlockPos center, char c, BiFunction<BlockPos, Matcher, Boolean> function) {
        BlockPos start = this.getStart(center);
        for (Map.Entry<BlockPos, Matcher> entry : this.matchers.entrySet()) {
            BlockPos offset = entry.getKey();
            if (c == 0 || this.getChar(offset) == c)
                if (!function.apply(start.add(offset), entry.getValue()))
                    return false;
        }
        return true;
    }

    public BlockPos getStart(BlockPos center) {
        return center.add(-this.xOffset, -this.yOffset, -this.zOffset);
    }

    public char getChar(BlockPos offset) {
        return this.rawPattern[offset.getX()][offset.getY()][offset.getZ()];
    }

    public static class Matcher {

        public final IBlockState defaultState;
        public final IMatcher check;
        public final boolean isWildcard;

        public Matcher(IBlockState defaultState, IMatcher check) {
            this.defaultState = defaultState;
            this.check = check;
            this.isWildcard = false;
        }

        public Matcher(IBlockState defaultState) {
            this.defaultState = defaultState;
            this.check = null;
            this.isWildcard = true;
        }

        public static Matcher state(IBlockState state) {
            return new Matcher(state,
                    (world, start, offset, pos, other, c) -> other == state);
        }

        public static Matcher block(Block block) {
            return new Matcher(block.getDefaultState(),
                    (world, start, offset, pos, state, c) -> state.getBlock() == block);
        }

        public static Matcher wildcard() {
            return new Matcher(Blocks.AIR.getDefaultState());
        }
    }

    public interface IMatcher {
        boolean matches(World world, BlockPos start, BlockPos offset, BlockPos pos, IBlockState state, char c);
    }
}
