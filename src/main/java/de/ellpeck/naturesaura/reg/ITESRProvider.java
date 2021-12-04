package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ITESRProvider<T extends BlockEntity> {

    @OnlyIn(Dist.CLIENT)
    Tuple<BlockEntityType<T>, Supplier<Function<? super BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<? super T>>>> getTESR();

}
