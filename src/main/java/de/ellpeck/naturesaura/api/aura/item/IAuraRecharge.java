package de.ellpeck.naturesaura.api.aura.item;

import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;

public interface IAuraRecharge {

    boolean rechargeFromContainer(IAuraContainer container, int containerSlot, int itemSlot, boolean isSelected);

}
