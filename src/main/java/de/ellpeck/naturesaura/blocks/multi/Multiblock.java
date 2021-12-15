package de.ellpeck.naturesaura.blocks.multi;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.compat.patchouli.PatchouliCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Multiblock implements IMultiblock {

    private final ResourceLocation name;
    private final Map<BlockPos, Matcher> matchers = new HashMap<>();
    private final int width;
    private final int height;
    private final int depth;
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;
    private final char[][][] rawPattern;

    public Multiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        this.name = name;

        var width = -1;
        this.height = pattern.length;
        var depth = -1;
        var xOff = 0;
        var yOff = 0;
        var zOff = 0;
        char[][][] raw = null;

        for (var i = 0; i < pattern.length; i++) {
            var row = pattern[i];

            if (width < 0)
                width = row.length;
            else if (row.length != width)
                throw new IllegalArgumentException();

            for (var j = 0; j < row.length; j++) {
                var column = row[j];
                if (depth < 0)
                    depth = column.length();
                else if (column.length() != depth)
                    throw new IllegalArgumentException();

                if (raw == null)
                    raw = new char[width][this.height][depth];
                for (var k = 0; k < column.length(); k++) {
                    var c = column.charAt(k);
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
        for (var i = 0; i < rawMatchers.length; i += 2) {
            var c = (char) rawMatchers[i];
            if (matchers.containsKey(c))
                continue;

            var value = rawMatchers[i + 1];
            if (value instanceof BlockState state) {
                matchers.put(c, new Matcher(state,
                        (level, start, offset, pos, other, otherC) -> other == state));
            } else if (value instanceof Block block) {
                matchers.put(c, new Matcher(block.defaultBlockState(),
                        (level, start, offset, pos, state, otherC) -> state.getBlock() == block));
            } else
                matchers.put(c, (Matcher) value);
        }

        for (var x = 0; x < this.width; x++)
            for (var y = 0; y < this.height; y++)
                for (var z = 0; z < this.depth; z++) {
                    var matcher = matchers.get(this.rawPattern[x][y][z]);
                    if (matcher == null)
                        throw new IllegalStateException();
                    if (matcher.check() != null)
                        this.matchers.put(new BlockPos(x, y, z), matcher);
                }

        PatchouliCompat.addPatchouliMultiblock(name, pattern, rawMatchers);
        NaturesAuraAPI.MULTIBLOCKS.put(this.name, this);
    }

    @Override
    public boolean isComplete(Level level, BlockPos center) {
        var start = this.getStart(center);
        return this.forEach(center, (char) 0, (pos, matcher) -> {
            var offset = pos.subtract(start);
            return matcher.check().matches(level, start, offset, pos, level.getBlockState(pos), this.getChar(offset));
        });
    }

    @Override
    public boolean forEach(BlockPos center, char c, BiFunction<BlockPos, Matcher, Boolean> function) {
        var start = this.getStart(center);
        for (var entry : this.matchers.entrySet()) {
            var offset = entry.getKey();
            if (c == 0 || this.getChar(offset) == c)
                if (!function.apply(start.offset(offset), entry.getValue()))
                    return false;
        }
        return true;
    }

    @Override
    public BlockPos getStart(BlockPos center) {
        return center.offset(-this.xOffset, -this.yOffset, -this.zOffset);
    }

    @Override
    public char getChar(BlockPos offset) {
        return this.rawPattern[offset.getX()][offset.getY()][offset.getZ()];
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public Map<BlockPos, Matcher> getMatchers() {
        return this.matchers;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    @Override
    public int getXOffset() {
        return this.xOffset;
    }

    @Override
    public int getYOffset() {
        return this.yOffset;
    }

    @Override
    public int getZOffset() {
        return this.zOffset;
    }

    @Override
    public char[][][] getRawPattern() {
        return this.rawPattern;
    }
}
