package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityRFConverter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BlockRFConverter extends BlockContainerImpl {

    public BlockRFConverter() {
        super("rf_converter", BlockEntityRFConverter.class, Properties.of(Material.STONE).sound(SoundType.STONE).strength(3));
    }

    @Override
    public String getDescriptionId() {
        return ModConfig.instance.rfConverter.get() ? super.getDescriptionId() : "block." + NaturesAura.MOD_ID + "." + this.getBaseName() + ".disabled";
    }
}
