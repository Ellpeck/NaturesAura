package de.ellpeck.naturesaura.reg;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITESRProvider<T extends BlockEntity> {

    @OnlyIn(Dist.CLIENT)
    void registerTESR();
}
