package de.ellpeck.naturesaura.commands;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
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
        if (args.length != 3) {
            throw new SyntaxErrorException("Wrong number of arguments");
        }

        String action = args[0];
        int amount;
        int range;
        try {
            amount = Integer.parseInt(args[1]);
            range = Integer.parseInt(args[2]);
        } catch (Exception e) {
            throw new NumberInvalidException("Invalid number");
        }

        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();

        if ("add".equals(action)) {
            BlockPos spot = AuraChunk.getLowestSpot(world, pos, range, pos);
            AuraChunk.getAuraChunk(world, spot).storeAura(spot, amount);
            sender.sendMessage(new TextComponentString("Added " + amount + " aura"));
        } else if ("remove".equals(action)) {
            BlockPos spot = AuraChunk.getHighestSpot(world, pos, range, pos);
            AuraChunk.getAuraChunk(world, spot).drainAura(spot, amount);
            sender.sendMessage(new TextComponentString("Removed " + amount + " aura"));
        } else {
            throw new SyntaxErrorException("Invalid action " + action);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "remove");
        } else {
            return Collections.emptyList();
        }
    }
}
