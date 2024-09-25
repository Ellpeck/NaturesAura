package de.ellpeck.naturesaura.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.api.render.ITrinketItem.RenderType;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class PlayerLayerTrinkets extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final Set<Item> alreadyRendered = new HashSet<>();

    public PlayerLayerTrinkets(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ModConfig.instance.renderItemsOnPlayer.get())
            return;
        if (player.getEffect(MobEffects.INVISIBILITY) != null)
            return;
        var main = player.getMainHandItem();
        var second = player.getOffhandItem();

        this.alreadyRendered.clear();
        matrixStackIn.pushPose();
        this.render(player, RenderType.BODY, main, second, matrixStackIn, bufferIn, packedLightIn);
        var yaw = player.yHeadRotO + (player.yHeadRot - player.yHeadRotO) * partialTicks;
        var yawOffset = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTicks;
        var pitch = player.xRotO + (player.getXRot() - player.xRotO) * partialTicks;
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(yawOffset));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 270));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(pitch));
        this.render(player, RenderType.HEAD, main, second, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    private void render(Player player, RenderType type, ItemStack main, ItemStack second, PoseStack matrices, MultiBufferSource buffer, int packedLight) {
        for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
            this.renderStack(player.getInventory().getItem(i), player, type, main, second, matrices, buffer, packedLight);
        }

        // TODO curios?
/*        if (Compat.hasCompat("curios")) {
            var inventory = CuriosApi.getCuriosInventory(player).orElse(null);
            if (inventory != null) {
                var handler = inventory.getEquippedCurios();
                for (var i = 0; i < handler.getSlots(); i++)
                    this.renderStack(handler.getStackInSlot(i), player, type, main, second, matrices, buffer, packedLight);
            }
        }*/
    }

    private void renderStack(ItemStack stack, Player player, RenderType type, ItemStack main, ItemStack second, PoseStack matrices, MultiBufferSource buffer, int packedLight) {
        if (!stack.isEmpty()) {
            var item = stack.getItem();
            if (item instanceof ITrinketItem && !this.alreadyRendered.contains(item)) {
                matrices.pushPose();
                if (type == RenderType.BODY && player.getPose() == Pose.CROUCHING) {
                    matrices.translate(0F, 0.2F, 0F);
                    matrices.mulPose(Axis.XP.rotationDegrees(90F / (float) Math.PI));
                }
                ((ITrinketItem) item).render(stack, player, type, matrices, buffer, packedLight, stack == main || stack == second);
                matrices.popPose();
                this.alreadyRendered.add(item);
            }
        }
    }

}
