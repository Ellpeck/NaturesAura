package de.ellpeck.naturesaura.events;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunkProvider;
import de.ellpeck.naturesaura.commands.CommandAura;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CommonEvents {

    private static final Method GET_LOADED_CHUNKS_METHOD = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
    private static final ListMultimap<UUID, ChunkPos> PENDING_AURA_CHUNKS = ArrayListMultimap.create();

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<Chunk> event) {
        Chunk chunk = event.getObject();
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "aura"), new AuraChunkProvider(chunk));
    }

    @SubscribeEvent
    public void onWorldCapsAttach(AttachCapabilitiesEvent<World> event) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "data"), new WorldData());
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        if (player.world.isRemote)
            return;
        ItemStack held = event.getItemStack();
        if (!held.isEmpty() && held.getItem().getRegistryName().getPath().contains("chisel")) {
            BlockState state = player.world.getBlockState(event.getPos());
            if (NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.containsKey(state)) {
                WorldData data = (WorldData) IWorldData.getWorldData(player.world);
                data.addMossStone(event.getPos());
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.world.getGameTime() % 20 == 0) {
                event.world.getProfiler().startSection(NaturesAura.MOD_ID + ":onWorldTick");
                try {
                    ChunkManager manager = ((ServerChunkProvider) event.world.getChunkProvider()).chunkManager;
                    Iterable<ChunkHolder> chunks = (Iterable<ChunkHolder>) GET_LOADED_CHUNKS_METHOD.invoke(manager);
                    for (ChunkHolder holder : chunks) {
                        Chunk chunk = holder.getChunkIfComplete();
                        if (chunk == null)
                            continue;
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
                        if (auraChunk != null)
                            auraChunk.update();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    NaturesAura.LOGGER.fatal(e);
                }
                event.world.getProfiler().endSection();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.player.world.getGameTime() % 10 == 0) {
                List<ChunkPos> pending = PENDING_AURA_CHUNKS.get(event.player.getUniqueID());
                pending.removeIf(p -> this.handleChunkWatchDeferred(event.player, p));
            }

            if (event.player.world.getGameTime() % 200 != 0)
                return;

            int aura = IAuraChunk.triangulateAuraInArea(event.player.world, event.player.getPosition(), 25);
            if (aura <= 0)
                Helper.addAdvancement(event.player, new ResourceLocation(NaturesAura.MOD_ID, "negative_imbalance"), "triggered_in_code");
            else if (aura >= 1500000)
                Helper.addAdvancement(event.player, new ResourceLocation(NaturesAura.MOD_ID, "positive_imbalance"), "triggered_in_code");
        }
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        PENDING_AURA_CHUNKS.put(event.getPlayer().getUniqueID(), event.getPos());
    }

    private boolean handleChunkWatchDeferred(PlayerEntity player, ChunkPos pos) {
        Chunk chunk = Helper.getLoadedChunk(player.world, pos.x, pos.z);
        if (chunk == null)
            return false;
        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
        if (auraChunk == null)
            return false;
        PacketHandler.sendTo(player, auraChunk.makePacket());
        return true;
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CommandAura.register(event.getServer().getCommandManager().getDispatcher());
    }
}
