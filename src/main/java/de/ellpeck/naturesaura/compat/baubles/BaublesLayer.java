package de.ellpeck.naturesaura.compat.baubles;

import baubles.api.BaublesApi;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class BaublesLayer implements LayerRenderer<EntityPlayer> {

    @Override
    public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (player.getActivePotionEffect(MobEffects.INVISIBILITY) != null)
            return;
        IItemHandler inv = BaublesApi.getBaublesHandler(player);

        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        this.render(inv, player, RenderType.BODY);
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yawOffset, 0, -1, 0);
        GlStateManager.rotate(yaw - 270, 0, 1, 0);
        GlStateManager.rotate(pitch, 0, 0, 1);
        this.render(inv, player, RenderType.HEAD);
        GlStateManager.popMatrix();

    }

    private void render(IItemHandler inv, EntityPlayer player, RenderType type) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (type == RenderType.BODY) {
                    boolean armor = !player.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex()).isEmpty();

                    GlStateManager.pushMatrix();
                    IRenderBauble.Helper.rotateIfSneaking(player);
                    if (item == ModItems.EYE) {
                        GlStateManager.translate(0.1F, 0.19F, armor ? -0.195F : -0.13F);
                        GlStateManager.scale(0.15F, 0.15F, 0.15F);
                        GlStateManager.rotate(180F, 1F, 0F, 0F);
                        Helper.renderItemInWorld(stack);
                    } else if (item == ModItems.AURA_CACHE) {
                        GlStateManager.translate(-0.15F, 0.65F, armor ? -0.195F : -0.13F);
                        GlStateManager.scale(0.25F, 0.25F, 0.25F);
                        GlStateManager.rotate(180F, 1F, 0F, 0F);
                        Helper.renderItemInWorld(stack);
                    }
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}