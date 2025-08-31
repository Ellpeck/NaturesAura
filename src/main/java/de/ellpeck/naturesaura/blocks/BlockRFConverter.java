package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityRFConverter;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.world.level.block.SoundType;

public class BlockRFConverter extends BlockContainerImpl implements IPickaxeBreakable {

    public BlockRFConverter() {
        super("rf_converter", BlockEntityRFConverter.class, Properties.of().sound(SoundType.STONE).strength(3));
    }

    @Override
    public String getDescriptionId() {
        return ModConfig.instance.rfConverter.get() ? super.getDescriptionId() : "block." + NaturesAura.MOD_ID + "." + this.getBaseName() + ".disabled";
    }
}
