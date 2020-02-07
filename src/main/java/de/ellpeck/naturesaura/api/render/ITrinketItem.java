package de.ellpeck.naturesaura.api.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITrinketItem {
    @OnlyIn(Dist.CLIENT)
    void render(ItemStack stack, PlayerEntity player, RenderType type, MatrixStack matrices, IRenderTypeBuffer buffer, int packedLight, boolean isHolding);

    enum RenderType {
        HEAD, BODY
    }

}
