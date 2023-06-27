package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public interface IDrainSpotEffect {

    void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot);

    boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type);

    ResourceLocation getName();

    default ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        return ActiveType.INACTIVE;
    }

    default ItemStack getDisplayIcon() {
        return ItemStack.EMPTY;
    }

    enum ActiveType {
        INACTIVE, INHIBITED, ACTIVE
    }
}
