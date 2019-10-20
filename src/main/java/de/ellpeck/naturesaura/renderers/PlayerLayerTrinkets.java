package de.ellpeck.naturesaura.renderers;

import baubles.api.BaublesApi;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.api.render.ITrinketItem.RenderType;
import de.ellpeck.naturesaura.compat.Compat;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class PlayerLayerTrinkets implements LayerRenderer<PlayerEntity> {

    private final Set<Item> alreadyRendered = new HashSet<>();

    @Override
    public void doRenderLayer(@Nonnull PlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (player.getActivePotionEffect(Effects.INVISIBILITY) != null)
            return;
        ItemStack main = player.getHeldItemMainhand();
        ItemStack second = player.getHeldItemOffhand();

        this.alreadyRendered.clear();
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        this.render(player, RenderType.BODY, main, second);
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yawOffset, 0, -1, 0);
        GlStateManager.rotate(yaw - 270, 0, 1, 0);
        GlStateManager.rotate(pitch, 0, 0, 1);
        this.render(player, RenderType.HEAD, main, second);
        GlStateManager.popMatrix();

    }

    private void render(PlayerEntity player, RenderType type, ItemStack main, ItemStack second) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            this.renderStack(player.inventory.getStackInSlot(i), player, type, main, second);
        }

        if (Compat.baubles) {
            IItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                this.renderStack(baubles.getStackInSlot(i), player, type, main, second);
            }
        }
    }

    private void renderStack(ItemStack stack, PlayerEntity player, RenderType type, ItemStack main, ItemStack second) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof ITrinketItem && !this.alreadyRendered.contains(item)) {
                GlStateManager.pushMatrix();
                if (type == RenderType.BODY && player.isSneaking()) {
                    GlStateManager.translate(0F, 0.2F, 0F);
                    GlStateManager.rotate(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
                }
                ((ITrinketItem) item).render(stack, player, type, stack == main || stack == second);
                GlStateManager.popMatrix();
                this.alreadyRendered.add(item);
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}