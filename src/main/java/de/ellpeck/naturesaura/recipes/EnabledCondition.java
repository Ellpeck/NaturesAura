package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonObject;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class EnabledCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "enabled");
    private ForgeConfigSpec.ConfigValue<Boolean> config;
    private final String name;

    @SuppressWarnings("unchecked")
    public EnabledCondition(String name) {
        this.name = name;
        try {
            this.config = (ForgeConfigSpec.ConfigValue<Boolean>) ModConfig.class.getField(name).get(ModConfig.instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            NaturesAura.LOGGER.error(e);
        }
    }

    @Override
    public ResourceLocation getID() {
        return EnabledCondition.NAME;
    }

    @Override
    public boolean test(IContext context) {
        return this.config != null && this.config.get();
    }

    public static class Serializer implements IConditionSerializer<EnabledCondition> {

        @Override
        public void write(JsonObject json, EnabledCondition value) {
            json.addProperty("config", value.name);
        }

        @Override
        public EnabledCondition read(JsonObject json) {
            return new EnabledCondition(GsonHelper.getAsString(json, "config"));
        }

        @Override
        public ResourceLocation getID() {
            return EnabledCondition.NAME;
        }
    }
}
