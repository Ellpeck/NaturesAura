package de.ellpeck.naturesaura.api.render;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITrinketItem {
    enum RenderType {
        HEAD, BODY
    }

    @OnlyIn(Dist.CLIENT)
    void render(ItemStack stack, PlayerEntity player, RenderType type, boolean isHolding);

}
