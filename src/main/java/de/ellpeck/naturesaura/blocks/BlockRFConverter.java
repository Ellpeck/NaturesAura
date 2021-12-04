package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityRFConverter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockRFConverter extends BlockContainerImpl {

    public BlockRFConverter() {
        super("rf_converter", BlockEntityRFConverter::new, Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(3));
    }

    @Override
    public String getTranslationKey() {
        return ModConfig.instance.rfConverter.get() ? super.getTranslationKey() : "block." + NaturesAura.MOD_ID + "." + this.getBaseName() + ".disabled";
    }
}
