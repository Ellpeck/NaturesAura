package de.ellpeck.naturesaura.entities.render;

import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderStub extends EntityRenderer<Entity> {
    public RenderStub(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public boolean shouldRender(Entity livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
