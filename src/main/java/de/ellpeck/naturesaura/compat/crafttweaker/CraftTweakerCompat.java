package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.actions.IAction;
import de.ellpeck.naturesaura.compat.ICompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CraftTweakerCompat implements ICompat {

    public static final List<Supplier<IAction>> SCHEDULED_ACTIONS = new ArrayList<>();

    @Override
    public void preInit() {

    }

    @Override
    public void preInitClient() {

    }

    @Override
    public void postInit() {
        for (Supplier<IAction> action : SCHEDULED_ACTIONS)
            CraftTweakerAPI.apply(action.get());
        SCHEDULED_ACTIONS.clear();
    }

    @Override
    public void addItemTags(ItemTagProvider provider) {

    }
}
