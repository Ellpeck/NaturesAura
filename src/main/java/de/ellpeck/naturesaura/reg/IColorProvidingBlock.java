package de.ellpeck.naturesaura.reg;

import net.minecraft.client.color.block.BlockColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IColorProvidingBlock {

    @OnlyIn(Dist.CLIENT)
    BlockColor getBlockColor();

}
