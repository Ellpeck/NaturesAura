package de.ellpeck.naturesaura.renderers;

import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets.RenderType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITrinketItem {

    @SideOnly(Side.CLIENT)
    void render(ItemStack stack, EntityPlayer player, RenderType type);
}
