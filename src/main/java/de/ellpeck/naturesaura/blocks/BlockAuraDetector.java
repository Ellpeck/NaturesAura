package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockAuraDetector extends BlockContainerImpl {

    public BlockAuraDetector() {
        super("aura_detector", BlockEntityAuraDetector.class, Properties.of(Material.STONE).strength(2F).sound(SoundType.STONE));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityAuraDetector detector)
            return detector.redstonePower;
        else
            return 0;
    }
}
