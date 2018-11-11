package de.ellpeck.naturesaura.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CraftTweakerCompat {

    public static final List<Supplier<IAction>> SCHEDULED_ACTIONS = new ArrayList<>();

    public static void postInit() {
        for (Supplier<IAction> action : SCHEDULED_ACTIONS)
            CraftTweakerAPI.apply(action.get());
        SCHEDULED_ACTIONS.clear();
    }
}
