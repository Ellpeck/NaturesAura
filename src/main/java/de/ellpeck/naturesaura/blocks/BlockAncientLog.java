package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class BlockAncientLog extends RotatedPillarBlock implements IModItem, ICustomBlockState {

    private final String baseName;

    public BlockAncientLog(String baseName) {
        super(Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F).sound(SoundType.WOOD));
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.logBlock(this);
    }
}
