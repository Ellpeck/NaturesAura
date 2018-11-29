package de.ellpeck.naturesaura.potion;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionImpl extends Potion implements IModItem {

    private static final ResourceLocation TEXTURE = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/potions.png");
    protected final String baseName;

    protected PotionImpl(String baseName, boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.baseName = baseName;

        ModRegistry.add(this);
    }

    @Override
    public Potion setIconIndex(int x, int y) {
        return super.setIconIndex(x, y);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        return super.getStatusIconIndex();
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }
}
