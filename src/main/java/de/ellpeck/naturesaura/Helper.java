package de.ellpeck.naturesaura;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Helper {

    public static boolean getBlockEntitiesInArea(LevelAccessor level, BlockPos pos, int radius, Function<BlockEntity, Boolean> consumer) {
        for (var x = pos.getX() - radius >> 4; x <= pos.getX() + radius >> 4; x++) {
            for (var z = pos.getZ() - radius >> 4; z <= pos.getZ() + radius >> 4; z++) {
                var chunk = getLoadedChunk(level, x, z);
                if (chunk != null) {
                    for (var tilePos : chunk.getBlockEntitiesPos()) {
                        if (tilePos.distSqr(pos) <= radius * radius)
                            if (consumer.apply(chunk.getBlockEntity(tilePos)))
                                return true;
                    }
                }
            }
        }
        return false;
    }

    public static void getAuraChunksWithSpotsInArea(Level level, BlockPos pos, int radius, Consumer<AuraChunk> consumer) {
        var data = (LevelData) ILevelData.getLevelData(level);
        for (var x = pos.getX() - radius >> 4; x <= pos.getX() + radius >> 4; x++) {
            for (var z = pos.getZ() - radius >> 4; z <= pos.getZ() + radius >> 4; z++) {
                var chunk = data.auraChunksWithSpots.get(ChunkPos.asLong(x, z));
                if (chunk != null)
                    consumer.accept(chunk);
            }
        }
    }

    public static List<ItemFrame> getAttachedItemFrames(Level level, BlockPos pos) {
        var frames = level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(0.25));
        for (var i = frames.size() - 1; i >= 0; i--) {
            var frame = frames.get(i);
            var framePos = frame.getPos().relative(frame.getDirection().getOpposite());
            if (!pos.equals(framePos))
                frames.remove(i);
        }
        return frames;
    }

    public static LevelChunk getLoadedChunk(LevelAccessor level, int x, int z) {
        // DO NOT EDIT PLEASE FOR THE LOVE OF GOD
        // This is very finicky and easily causes the game to hang for some reason
        var provider = level.getChunkSource();
        if (provider.hasChunk(x, z))
            return provider.getChunk(x, z, false);
        return null;
    }

    public static int blendColors(int c1, int c2, float ratio) {
        var a = (int) ((c1 >> 24 & 0xFF) * ratio + (c2 >> 24 & 0xFF) * (1 - ratio));
        var r = (int) ((c1 >> 16 & 0xFF) * ratio + (c2 >> 16 & 0xFF) * (1 - ratio));
        var g = (int) ((c1 >> 8 & 0xFF) * ratio + (c2 >> 8 & 0xFF) * (1 - ratio));
        var b = (int) ((c1 & 0xFF) * ratio + (c2 & 0xFF) * (1 - ratio));
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
    }

    public static boolean areItemsEqual(ItemStack first, ItemStack second, boolean nbt) {
        if (!ItemStack.isSame(first, second))
            return false;
        return !nbt || ItemStack.tagMatches(first, second);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderItemInGui(ItemStack stack, int x, int y, float scale) {
        var poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, 0, 0);
        Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, 0, 0, null);
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderWeirdBox(VertexConsumer buffer, double x, double y, double z, double width, double height, double depth, float r, float g, float b, float a) {
        buffer.vertex(x, y + height, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y + height, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y, z).color(r, g, b, a).endVertex();
        buffer.vertex(x, y, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y + height, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y + height, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y + height, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y + height, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y + height, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y + height, z).color(r, g, b, a).endVertex();
        buffer.vertex(x, y, z).color(r, g, b, a).endVertex();
        buffer.vertex(x, y, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y + height, z).color(r, g, b, a).endVertex();
        buffer.vertex(x, y + height, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y + height, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y + height, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y, z).color(r, g, b, a).endVertex();
        buffer.vertex(x + width, y, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y, z + depth).color(r, g, b, a).endVertex();
        buffer.vertex(x, y, z).color(r, g, b, a).endVertex();
    }

    public static InteractionResult putStackOnTile(Player player, InteractionHand hand, BlockPos pos, int slot, boolean sound) {
        var tile = player.level.getBlockEntity(pos);
        if (tile instanceof BlockEntityImpl) {
            var handler = ((BlockEntityImpl) tile).getItemHandler();
            if (handler != null) {
                var handStack = player.getItemInHand(hand);
                if (!handStack.isEmpty()) {
                    var remain = handler.insertItem(slot, handStack, player.level.isClientSide);
                    if (!ItemStack.matches(remain, handStack)) {
                        if (sound)
                            player.level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.75F, 1F);
                        if (!player.level.isClientSide)
                            player.setItemInHand(hand, remain);
                        return InteractionResult.SUCCESS;
                    }
                }

                if (!handler.getStackInSlot(slot).isEmpty()) {
                    if (sound)
                        player.level.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 0.75F, 1F);
                    if (!player.level.isClientSide) {
                        var stack = handler.getStackInSlot(slot);
                        if (!player.addItem(stack)) {
                            var item = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack);
                            player.level.addFreshEntity(item);
                        }
                        handler.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    public static ICapabilityProvider makeRechargeProvider(ItemStack stack, boolean needsSelected) {
        return new ICapabilityProvider() {
            private final LazyOptional<IAuraRecharge> recharge = LazyOptional.of(() -> (container, containerSlot, itemSlot, isSelected) -> {
                if (isSelected || !needsSelected)
                    return rechargeAuraItem(stack, container, 300);
                return false;
            });

            @Nullable
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                if (capability == NaturesAuraAPI.capAuraRecharge)
                    return this.recharge.cast();
                return LazyOptional.empty();
            }
        };
    }

    public static boolean rechargeAuraItem(ItemStack stack, IAuraContainer container, int toDrain) {
        if (stack.getDamageValue() > 0 && container.drainAura(toDrain, true) >= toDrain) {
            stack.setDamageValue(stack.getDamageValue() - 1);
            container.drainAura(toDrain, false);
            return true;
        }
        return false;
    }

    public static BlockState getStateFromString(String raw) {
        var split = raw.split("\\[");
        var block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0]));
        if (block != null) {
            var state = block.defaultBlockState();
            if (split.length > 1) {
                for (var part : split[1].replace("]", "").split(",")) {
                    var keyValue = part.split("=");
                    for (var prop : state.getProperties()) {
                        var changed = findProperty(state, prop, keyValue[0], keyValue[1]);
                        if (changed != null) {
                            state = changed;
                            break;
                        }
                    }
                }
            }
            return state;
        } else
            return null;
    }

    private static <T extends Comparable<T>> BlockState findProperty(BlockState state, Property<T> prop, String key, String newValue) {
        if (key.equals(prop.getName()))
            for (var value : prop.getPossibleValues())
                if (prop.getName(value).equals(newValue))
                    return state.setValue(prop, value);
        return null;
    }

    public static void addAdvancement(Player player, ResourceLocation advancement, String criterion) {
        if (!(player instanceof ServerPlayer playerMp))
            return;
        var adv = playerMp.getLevel().getServer().getAdvancements().getAdvancement(advancement);
        if (adv != null)
            playerMp.getAdvancements().award(adv, criterion);
    }

    public static int getIngredientAmount(Ingredient ingredient) {
        var highestAmount = 0;
        for (var stack : ingredient.getItems())
            if (stack.getCount() > highestAmount)
                highestAmount = stack.getCount();
        return highestAmount;
    }

    public static boolean isHoldingItem(Player player, Item item) {
        for (var hand : InteractionHand.values()) {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && stack.getItem() == item)
                return true;
        }
        return false;
    }

    public static boolean isEmpty(IItemHandler handler) {
        for (var i = 0; i < handler.getSlots(); i++)
            if (!handler.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    public static AABB aabb(Vec3 pos) {
        return new AABB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z);
    }

    // This is how @ObjectHolder SHOULD work...
    public static <T extends IForgeRegistryEntry<T>> void populateObjectHolders(Class<?> clazz, IForgeRegistry<T> registry) {
        for (var entry : clazz.getFields()) {
            if (!Modifier.isStatic(entry.getModifiers()))
                continue;
            var location = new ResourceLocation(NaturesAura.MOD_ID, entry.getName().toLowerCase(Locale.ROOT));
            if (!registry.containsKey(location)) {
                NaturesAura.LOGGER.fatal("Couldn't find entry named " + location + " in registry " + registry.getRegistryName());
                continue;
            }
            try {
                entry.set(null, registry.getValue(location));
            } catch (IllegalAccessException e) {
                NaturesAura.LOGGER.error(e);
            }
        }
    }

    public static ItemStack getEquippedItem(Predicate<ItemStack> predicate, Player player) {
        if (Compat.hasCompat("curios")) {
            var stack = CuriosApi.getCuriosHelper().findEquippedCurio(predicate, player).map(ImmutableTriple::getRight);
            if (stack.isPresent())
                return stack.get();
        }
        for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
            var slot = player.getInventory().getItem(i);
            if (!slot.isEmpty() && predicate.test(slot))
                return slot;
        }
        return ItemStack.EMPTY;
    }
}
