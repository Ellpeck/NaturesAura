package de.ellpeck.naturesaura.renderers;

import baubles.api.BaublesApi;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class PlayerLayerTrinkets implements LayerRenderer<EntityPlayer> {

    private final Set<Item> alreadyRendered = new HashSet<>();

    @Override
    public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (player.getActivePotionEffect(MobEffects.INVISIBILITY) != null)
            return;

        this.alreadyRendered.clear();
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        this.render(player, RenderType.BODY);
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yawOffset, 0, -1, 0);
        GlStateManager.rotate(yaw - 270, 0, 1, 0);
        GlStateManager.rotate(pitch, 0, 0, 1);
        this.render(player, RenderType.HEAD);
        GlStateManager.popMatrix();

    }

    private void render(EntityPlayer player, RenderType type) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            this.renderStack(player.inventory.getStackInSlot(i), player, type);
        }

        if (Compat.baubles) {
            IItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                this.renderStack(baubles.getStackInSlot(i), player, type);
            }
        }
    }

    private void renderStack(ItemStack stack, EntityPlayer player, RenderType type) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof ITrinketItem && !this.alreadyRendered.contains(item)) {
                GlStateManager.pushMatrix();
                ((ITrinketItem) item).render(stack, player, type);
                GlStateManager.popMatrix();
                this.alreadyRendered.add(item);
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public enum RenderType {
        HEAD, BODY
    }
}