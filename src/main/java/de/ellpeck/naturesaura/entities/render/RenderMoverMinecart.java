package de.ellpeck.naturesaura.entities.render;

import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;

public class RenderMoverMinecart extends RenderMinecart<EntityMoverMinecart> {
    public RenderMoverMinecart(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void renderCartContents(EntityMoverMinecart cart, float partialTicks, IBlockState state) {

    }
}
