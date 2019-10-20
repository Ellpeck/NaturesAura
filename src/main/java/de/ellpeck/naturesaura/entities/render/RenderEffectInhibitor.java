package de.ellpeck.naturesaura.entities.render;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderEffectInhibitor extends EntityRenderer<EntityEffectInhibitor> {

    private final Map<ResourceLocation, ItemStack> items = new HashMap<>();

    public RenderEffectInhibitor(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityEffectInhibitor entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public void doRender(EntityEffectInhibitor entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        float time = entity.renderTicks + entity.getEntityId() + partialTicks;
        float bob = (float) Math.sin(time / 10F) * 0.05F;
        GlStateManager.translate(x, y + 0.15F + bob, z);
        GlStateManager.rotate((time * 3) % 360, 0F, 1F, 0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        ResourceLocation effect = entity.getInhibitedEffect();
        Helper.renderItemInWorld(this.items.computeIfAbsent(effect,
                res -> ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER), effect)));
        GlStateManager.popMatrix();
    }
}
