package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public interface ITESRProvider<T extends BlockEntity> {

    @OnlyIn(Dist.CLIENT)
    Tuple<BlockEntityType<? extends T>, Supplier<BlockEntityRendererProvider<T>>> getTESR();
}
