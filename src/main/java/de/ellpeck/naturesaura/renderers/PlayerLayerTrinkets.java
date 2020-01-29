package de.ellpeck.naturesaura.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.api.render.ITrinketItem.RenderType;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class PlayerLayerTrinkets extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final Set<Item> alreadyRendered = new HashSet<>();

    public PlayerLayerTrinkets(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    // TODO Fix this
    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.getActivePotionEffect(Effects.INVISIBILITY) != null)
            return;
        ItemStack main = player.getHeldItemMainhand();
        ItemStack second = player.getHeldItemOffhand();

        this.alreadyRendered.clear();
        RenderSystem.pushMatrix();
        RenderSystem.pushLightingAttributes();
        RenderSystem.pushTextureAttributes();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        this.render(player, RenderType.BODY, main, second);
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        RenderSystem.rotatef(yawOffset, 0, -1, 0);
        RenderSystem.rotatef(yaw - 270, 0, 1, 0);
        RenderSystem.rotatef(pitch, 0, 0, 1);
        this.render(player, RenderType.HEAD, main, second);
        RenderSystem.popAttributes();
        RenderSystem.popAttributes();
        RenderSystem.popMatrix();
    }

    private void render(PlayerEntity player, RenderType type, ItemStack main, ItemStack second) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            this.renderStack(player.inventory.getStackInSlot(i), player, type, main, second);
        }

        if (Compat.hasCompat("curios")) {
            ICurioItemHandler handler = CuriosAPI.getCuriosHandler(player).orElse(null);
            if (handler != null) {
                for (IItemHandler items : handler.getCurioMap().values()) {
                    for (int i = 0; i < items.getSlots(); i++) {
                        this.renderStack(items.getStackInSlot(i), player, type, main, second);
                    }
                }
            }
        }
    }

    private void renderStack(ItemStack stack, PlayerEntity player, RenderType type, ItemStack main, ItemStack second) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof ITrinketItem && !this.alreadyRendered.contains(item)) {
                GlStateManager.pushMatrix();
                if (type == RenderType.BODY && player.isShiftKeyDown()) {
                    GlStateManager.translatef(0F, 0.2F, 0F);
                    GlStateManager.rotatef(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
                }
                ((ITrinketItem) item).render(stack, player, type, stack == main || stack == second);
                GlStateManager.popMatrix();
                this.alreadyRendered.add(item);
            }
        }
    }
}