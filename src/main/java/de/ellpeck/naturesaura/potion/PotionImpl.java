package de.ellpeck.naturesaura.potion;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class PotionImpl extends Effect implements IModItem {

    private static final ResourceLocation TEXTURE = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/potions.png");
    protected final String baseName;

    protected PotionImpl(String baseName, EffectType type, int liquidColorIn) {
        super(type, liquidColorIn);
        this.baseName = baseName;

        ModRegistry.add(this);
    }

    /* TODO potion textures

    @Override
    public Effect setIconIndex(int x, int y) {
        return super.setIconIndex(x, y);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        return super.getStatusIconIndex();
    }*/

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
