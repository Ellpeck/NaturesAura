package de.ellpeck.naturesaura.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ITrinketItem {

    @OnlyIn(Dist.CLIENT)
    void render(ItemStack stack, Player player, RenderType type, PoseStack matrices, MultiBufferSource buffer, int packedLight, boolean isHolding);

    enum RenderType {
        HEAD, BODY
    }

}
