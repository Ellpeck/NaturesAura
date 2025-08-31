package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraDetector;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockAuraDetector extends BlockContainerImpl implements IPickaxeBreakable {

    public BlockAuraDetector() {
        super("aura_detector", BlockEntityAuraDetector.class, Properties.of().strength(2F).sound(SoundType.STONE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityAuraDetector detector)
            return detector.redstonePower;
        else
            return 0;
    }
}
