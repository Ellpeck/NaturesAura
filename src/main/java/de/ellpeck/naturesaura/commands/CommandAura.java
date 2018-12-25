package de.ellpeck.naturesaura.commands;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandAura extends CommandBase {
    @Override
    public String getName() {
        return "naaura";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command." + NaturesAura.MOD_ID + ".aura.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2)
            throw new WrongUsageException(this.getUsage(sender));
        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();

        String action = args[0];
        if ("store".equals(action)) {
            int amount = parse(args, 1, -1);
            int range = parse(args, 2, 35);
            while (amount > 0) {
                BlockPos spot = IAuraChunk.getLowestSpot(world, pos, range, pos);
                amount -= IAuraChunk.getAuraChunk(world, spot).storeAura(spot, amount);
            }
            sender.sendMessage(new TextComponentString("Stored Aura successfully"));
        } else if ("drain".equals(action)) {
            int amount = parse(args, 1, -1);
            int range = parse(args, 2, 35);
            while (amount > 0) {
                BlockPos spot = IAuraChunk.getHighestSpot(world, pos, range, pos);
                amount -= IAuraChunk.getAuraChunk(world, spot).drainAura(spot, amount);
            }
            sender.sendMessage(new TextComponentString("Drained Aura successfully"));
        } else if ("reset".equals(action)) {
            int range = parse(args, 1, -1);
            IAuraChunk.getSpotsInArea(world, pos, range, (spot, amount) -> {
                IAuraChunk chunk = IAuraChunk.getAuraChunk(world, spot);
                if (amount > 0)
                    chunk.drainAura(spot, amount);
                else
                    chunk.storeAura(spot, -amount);
            });
            sender.sendMessage(new TextComponentString("Reset Aura successfully"));
        } else {
            throw new SyntaxErrorException("Invalid action " + action);
        }
    }

    private static int parse(String[] args, int argNum, int def) throws NumberInvalidException {
        try {
            return Integer.parseInt(args[argNum]);
        } catch (Exception e) {
            if (def < 0)
                throw new NumberInvalidException("Invalid number " + args[argNum]);
            else
                return def;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "store", "drain", "reset");
        } else {
            return Collections.emptyList();
        }
    }
}
