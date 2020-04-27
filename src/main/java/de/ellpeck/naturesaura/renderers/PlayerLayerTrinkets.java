package de.ellpeck.naturesaura.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.api.render.ITrinketItem.RenderType;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Pose;
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

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ModConfig.instance.renderItemsOnPlayer.get())
            return;
        if (player.getActivePotionEffect(Effects.INVISIBILITY) != null)
            return;
        ItemStack main = player.getHeldItemMainhand();
        ItemStack second = player.getHeldItemOffhand();

        this.alreadyRendered.clear();
        matrixStackIn.push();
        this.render(player, RenderType.BODY, main, second, matrixStackIn, bufferIn, packedLightIn);
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(yawOffset));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(yaw - 270));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(pitch));
        this.render(player, RenderType.HEAD, main, second, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }

    private void render(PlayerEntity player, RenderType type, ItemStack main, ItemStack second, MatrixStack matrices, IRenderTypeBuffer buffer, int packedLight) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            this.renderStack(player.inventory.getStackInSlot(i), player, type, main, second, matrices, buffer, packedLight);
        }

        if (Compat.hasCompat("curios")) {
            ICurioItemHandler handler = CuriosAPI.getCuriosHandler(player).orElse(null);
            if (handler != null) {
                for (IItemHandler items : handler.getCurioMap().values()) {
                    for (int i = 0; i < items.getSlots(); i++) {
                        this.renderStack(items.getStackInSlot(i), player, type, main, second, matrices, buffer, packedLight);
                    }
                }
            }
        }
    }

    private void renderStack(ItemStack stack, PlayerEntity player, RenderType type, ItemStack main, ItemStack second, MatrixStack matrices, IRenderTypeBuffer buffer, int packedLight) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof ITrinketItem && !this.alreadyRendered.contains(item)) {
                matrices.push();
                if (type == RenderType.BODY && player.getPose() == Pose.CROUCHING) {
                    matrices.translate(0F, 0.2F, 0F);
                    matrices.rotate(Vector3f.XP.rotationDegrees(90F / (float) Math.PI));
                }
                ((ITrinketItem) item).render(stack, player, type, matrices, buffer, packedLight, stack == main || stack == second);
                matrices.pop();
                this.alreadyRendered.add(item);
            }
        }
    }
}