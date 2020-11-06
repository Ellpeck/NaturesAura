package de.ellpeck.naturesaura.renderers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class SupporterFancyHandler {

    public static final Map<String, FancyInfo> FANCY_INFOS = new HashMap<>();

    public SupporterFancyHandler() {
        new FetchThread();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        PlayerEntity player = event.player;
        if (!player.world.isRemote)
            return;
        if (player.isInvisible() || !player.isWearing(PlayerModelPart.CAPE))
            return;
        Minecraft mc = Minecraft.getInstance();
        if (player == mc.player && mc.gameSettings.func_243230_g() == PointOfView.FIRST_PERSON)
            return;
        FancyInfo info = FANCY_INFOS.get(player.getGameProfile().getName());
        if (info == null)
            return;

        Random rand = player.world.rand;
        if (rand.nextFloat() >= 0.75F) {
            int color;
            if (info.tier == 1) {
                BlockPos pos = player.getPosition();
                color = BiomeColors.getGrassColor(player.world, pos);
            } else {
                color = info.color;
            }

            NaturesAuraAPI.instance().spawnMagicParticle(
                    player.getPosX() + rand.nextGaussian() * 0.15F,
                    player.getPosY() + rand.nextFloat() * 1.8F,
                    player.getPosZ() + rand.nextGaussian() * 0.15F,
                    rand.nextGaussian() * 0.01F,
                    rand.nextFloat() * 0.01F,
                    rand.nextGaussian() * 0.01F,
                    color, rand.nextFloat() + 1F, rand.nextInt(50) + 50, 0F, false, true);
        }
    }

    public static class FancyInfo {
        public final int tier;
        public final int color;

        public FancyInfo(int tier, int color) {
            this.tier = tier;
            this.color = color;
        }
    }

    private static class FetchThread extends Thread {
        public FetchThread() {
            this.setName(NaturesAura.MOD_ID + "_support_fetcher");
            this.setDaemon(true);
            this.start();
        }

        @Override
        public void run() {
            try {
                URL url = new URL("https://raw.githubusercontent.com/Ellpeck/NaturesAura/main/supporters.json");
                JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
                JsonParser parser = new JsonParser();

                JsonObject main = parser.parse(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : main.entrySet()) {
                    JsonObject object = entry.getValue().getAsJsonObject();
                    int tier = object.get("tier").getAsInt();
                    int color = object.has("color") ? Integer.parseInt(object.get("color").getAsString(), 16) : 0;
                    FANCY_INFOS.put(entry.getKey(), new FancyInfo(tier, color));
                }

                reader.close();
            } catch (Exception e) {
                NaturesAura.LOGGER.warn("Fetching supporter information failed", e);
            }
        }
    }
}
