package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITESRProvider {

    @OnlyIn(Dist.CLIENT)
    Tuple<Class, TileEntityRenderer> getTESR();

}
