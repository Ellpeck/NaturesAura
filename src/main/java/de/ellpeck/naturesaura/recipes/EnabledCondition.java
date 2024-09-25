package de.ellpeck.naturesaura.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

public class EnabledCondition implements ICondition {

    private static final MapCodec<EnabledCondition> CODEC = RecordCodecBuilder.mapCodec(i ->
        i.group(Codec.STRING.fieldOf("name").forGetter(c -> c.name)).apply(i, EnabledCondition::new)
    );

    private ModConfigSpec.ConfigValue<Boolean> config;
    private final String name;

    @SuppressWarnings("unchecked")
    public EnabledCondition(String name) {
        this.name = name;
        try {
            this.config = (ModConfigSpec.ConfigValue<Boolean>) ModConfig.class.getField(name).get(ModConfig.instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            NaturesAura.LOGGER.error(e);
        }
    }

    @Override
    public boolean test(IContext context) {
        return this.config != null && this.config.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return EnabledCondition.CODEC;
    }

}
