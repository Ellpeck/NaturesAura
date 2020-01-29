package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.RenderType;

import java.util.function.Supplier;

public interface ICustomRenderType {

    Supplier<RenderType> getRenderType();
}
