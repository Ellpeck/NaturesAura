package de.ellpeck.naturesaura.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public final class CommandAura {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("naaura").requires(s -> s.hasPermissionLevel(2))
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
                    int amount = IntegerArgumentType.getInteger(context, "amount");
                    CommandSource source = context.getSource();
                    BlockPos pos = new BlockPos(source.getPos());
                    while (amount > 0) {
                        BlockPos spot = IAuraChunk.getLowestSpot(source.getWorld(), pos, 35, pos);
                        amount -= IAuraChunk.getAuraChunk(source.getWorld(), spot).storeAura(spot, amount);
                    }
                    source.sendFeedback(new StringTextComponent("Added aura to area"), true);
                    return 0;
                })))
                .then(Commands.literal("remove").then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
                    int amount = IntegerArgumentType.getInteger(context, "amount");
                    CommandSource source = context.getSource();
                    BlockPos pos = new BlockPos(source.getPos());
                    while (amount > 0) {
                        BlockPos spot = IAuraChunk.getHighestSpot(source.getWorld(), pos, 35, pos);
                        amount -= IAuraChunk.getAuraChunk(source.getWorld(), spot).drainAura(spot, amount);
                    }
                    source.sendFeedback(new StringTextComponent("Removed aura from area"), true);
                    return 0;
                })))
                .then(Commands.literal("reset").then(Commands.argument("range", IntegerArgumentType.integer(1)).executes(context -> {
                    int range = IntegerArgumentType.getInteger(context, "range");
                    CommandSource source = context.getSource();
                    BlockPos pos = new BlockPos(source.getPos());
                    IAuraChunk.getSpotsInArea(source.getWorld(), pos, range, (spot, amount) -> {
                        IAuraChunk chunk = IAuraChunk.getAuraChunk(source.getWorld(), spot);
                        if (amount > 0)
                            chunk.drainAura(spot, amount);
                        else
                            chunk.storeAura(spot, -amount);
                    });
                    source.sendFeedback(new StringTextComponent("Reset aura in area"), true);
                    return 0;
                }))));
    }
}
