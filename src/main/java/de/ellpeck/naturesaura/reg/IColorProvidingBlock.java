package de.ellpeck.naturesaura.reg;

import net.minecraft.client.color.block.BlockColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IColorProvidingBlock {

    @OnlyIn(Dist.CLIENT)
    BlockColor getBlockColor();

}
