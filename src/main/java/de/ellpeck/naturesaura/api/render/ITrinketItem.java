package de.ellpeck.naturesaura.api.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITrinketItem {
    enum RenderType {
        HEAD, BODY
    }

    @SideOnly(Side.CLIENT)
    void render(ItemStack stack, EntityPlayer player, RenderType type, boolean isHolding);

}
