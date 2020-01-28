package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ITESRProvider<T extends TileEntity> {

    @OnlyIn(Dist.CLIENT)
    Tuple<TileEntityType<T>, Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>>>> getTESR();

}
