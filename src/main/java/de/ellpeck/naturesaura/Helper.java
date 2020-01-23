package de.ellpeck.naturesaura;

import com.mojang.blaze3d.platform.GlStateManager;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.INBT;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Helper {

    public static boolean getTileEntitiesInArea(IWorld world, BlockPos pos, int radius, Function<TileEntity, Boolean> consumer) {
        for (int x = (pos.getX() - radius) >> 4; x <= (pos.getX() + radius) >> 4; x++) {
            for (int z = (pos.getZ() - radius) >> 4; z <= (pos.getZ() + radius) >> 4; z++) {
                Chunk chunk = getLoadedChunk(world, x, z);
                if (chunk != null) {
                    for (BlockPos tilePos : chunk.getTileEntitiesPos()) {
                        if (tilePos.distanceSq(pos) <= radius * radius)
                            if (consumer.apply(world.getTileEntity(tilePos)))
                                return true;
                    }
                }
            }
        }
        return false;
    }

    public static void getAuraChunksInArea(World world, BlockPos pos, int radius, Consumer<AuraChunk> consumer) {
        for (int x = (pos.getX() - radius) >> 4; x <= (pos.getX() + radius) >> 4; x++) {
            for (int z = (pos.getZ() - radius) >> 4; z <= (pos.getZ() + radius) >> 4; z++) {
                Chunk chunk = getLoadedChunk(world, x, z);
                if (chunk != null) {
                    AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
                    if (auraChunk != null)
                        consumer.accept(auraChunk);
                }
            }
        }
    }

    public static List<ItemFrameEntity> getAttachedItemFrames(World world, BlockPos pos) {
        List<ItemFrameEntity> frames = world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(pos).grow(0.25));
        for (int i = frames.size() - 1; i >= 0; i--) {
            ItemFrameEntity frame = frames.get(i);
            BlockPos framePos = frame.getHangingPosition().offset(frame.getHorizontalFacing().getOpposite());
            if (!pos.equals(framePos))
                frames.remove(i);
        }
        return frames;
    }

    public static Chunk getLoadedChunk(IWorld world, int x, int z) {
        // DO NOT EDIT PLEASE FOR THE LOVE OF GOD
        // This is very finicky and easily causes the game to hang for some reason
        AbstractChunkProvider provider = world.getChunkProvider();
        if (provider.isChunkLoaded(new ChunkPos(x, z)))
            return provider.getChunk(x, z, false);
        return null;
    }

    public static int blendColors(int c1, int c2, float ratio) {
        int a = (int) ((c1 >> 24 & 0xFF) * ratio + (c2 >> 24 & 0xFF) * (1 - ratio));
        int r = (int) ((c1 >> 16 & 0xFF) * ratio + (c2 >> 16 & 0xFF) * (1 - ratio));
        int g = (int) ((c1 >> 8 & 0xFF) * ratio + (c2 >> 8 & 0xFF) * (1 - ratio));
        int b = (int) ((c1 & 0xFF) * ratio + (c2 & 0xFF) * (1 - ratio));
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static boolean areItemsEqual(ItemStack first, ItemStack second, boolean nbt) {
        if (!ItemStack.areItemsEqual(first, second))
            return false;
        return !nbt || ItemStack.areItemStackTagsEqual(first, second);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderItemInWorld(ItemStack stack) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.pushTextureAttributes();
            GlStateManager.pushLightingAttributes();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttributes();
            GlStateManager.popAttributes();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderItemInGui(ItemStack stack, int x, int y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepthTest();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translatef(x, y, 0);
        GlStateManager.scalef(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        Minecraft.getInstance().getItemRenderer().renderItemOverlayIntoGUI(Minecraft.getInstance().fontRenderer, stack, 0, 0, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static boolean putStackOnTile(PlayerEntity player, Hand hand, BlockPos pos, int slot, boolean sound) {
        TileEntity tile = player.world.getTileEntity(pos);
        if (tile instanceof TileEntityImpl) {
            IItemHandlerModifiable handler = ((TileEntityImpl) tile).getItemHandler(null);
            if (handler != null) {
                ItemStack handStack = player.getHeldItem(hand);
                if (!handStack.isEmpty()) {
                    ItemStack remain = handler.insertItem(slot, handStack, player.world.isRemote);
                    if (!ItemStack.areItemStacksEqual(remain, handStack)) {
                        if (sound)
                            player.world.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.PLAYERS, 0.75F, 1F);
                        if (!player.world.isRemote)
                            player.setHeldItem(hand, remain);
                        return true;
                    }
                }

                if (!handler.getStackInSlot(slot).isEmpty()) {
                    if (sound)
                        player.world.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 0.75F, 1F);
                    if (!player.world.isRemote) {
                        ItemStack stack = handler.getStackInSlot(slot);
                        if (!player.addItemStackToInventory(stack)) {
                            ItemEntity item = new ItemEntity(player.world, player.posX, player.posY, player.posZ, stack);
                            player.world.addEntity(item);
                        }
                        handler.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static ICapabilityProvider makeRechargeProvider(ItemStack stack, boolean needsSelected) {
        return new ICapabilityProvider() {
            private final IAuraRecharge recharge = (container, containerSlot, itemSlot, isSelected) -> {
                if (isSelected || !needsSelected) {
                    int toDrain = 300;
                    if (stack.getDamage() > 0 && container.drainAura(toDrain, true) >= toDrain) {
                        stack.setDamage(stack.getDamage() - 1);
                        container.drainAura(toDrain, false);
                        return true;
                    }
                }
                return false;
            };

            @Nullable
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                if (capability == NaturesAuraAPI.capAuraRecharge)
                    return LazyOptional.of(() -> (T) this.recharge);
                return LazyOptional.empty();
            }
        };
    }

    public static BlockState getStateFromString(String raw) {
        String[] split = raw.split("\\[");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0]));
        if (block != null) {
            BlockState state = block.getDefaultState();
            if (split.length > 1) {
                for (String part : split[1].replace("]", "").split(",")) {
                    String[] keyValue = part.split("=");
                    for (IProperty<?> prop : state.getProperties()) {
                        BlockState changed = findProperty(state, prop, keyValue[0], keyValue[1]);
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

    private static <T extends Comparable<T>> BlockState findProperty(BlockState state, IProperty<T> prop, String key, String newValue) {
        if (key.equals(prop.getName()))
            for (T value : prop.getAllowedValues())
                if (prop.getName(value).equals(newValue))
                    return state.with(prop, value);
        return null;
    }

    public static <T> void registerCap(Class<T> type) {
        CapabilityManager.INSTANCE.register(type, new Capability.IStorage<T>() {
            @Override
            public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

            }

            @Nullable
            @Override
            public INBT writeNBT(Capability capability, Object instance, Direction side) {
                return null;
            }
        }, () -> null);
    }

    public static void addAdvancement(PlayerEntity player, ResourceLocation advancement, String criterion) {
        if (!(player instanceof ServerPlayerEntity))
            return;/* TODO add advancements
        ServerPlayerEntity playerMp = (ServerPlayerEntity) player;
        Advancement adv = playerMp.getServerWorld().getAdvancementManager().getAdvancement(advancement);
        if (adv != null)
            playerMp.getAdvancements().grantCriterion(adv, criterion);*/
    }

    public static int getIngredientAmount(Ingredient ingredient) {
        int highestAmount = 0;
        for (ItemStack stack : ingredient.getMatchingStacks())
            if (stack.getCount() > highestAmount)
                highestAmount = stack.getCount();
        return highestAmount;
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderWeirdBox(double x, double y, double z, double width, double height, double depth) {
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x + width, y, z + depth);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y + height, z + depth);
        GL11.glVertex3d(x + width, y + height, z + depth);
        GL11.glVertex3d(x, y + height, z + depth);
        GL11.glVertex3d(x, y, z + depth);
        GL11.glVertex3d(x + width, y, z + depth);
        GL11.glVertex3d(x, y + height, z + depth);
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z + depth);
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x, y + height, z + depth);
        GL11.glVertex3d(x + width, y + height, z + depth);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x + width, y, z + depth);
        GL11.glVertex3d(x, y, z + depth);
        GL11.glVertex3d(x, y, z);
    }

    public static boolean isHoldingItem(PlayerEntity player, Item item) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty() && stack.getItem() == item)
                return true;
        }
        return false;
    }

    public static boolean isEmpty(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++)
            if (!handler.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    public static AxisAlignedBB aabb(Vec3d pos) {
        return new AxisAlignedBB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z);
    }

    // This is how @ObjectHolder _SHOULD_ work...
    public static <T extends IForgeRegistryEntry<T>> void populateObjectHolders(Class clazz, IForgeRegistry<T> registry) {
        for (Field entry : clazz.getFields()) {
            if (!Modifier.isStatic(entry.getModifiers()))
                continue;
            String location = entry.getName().toLowerCase(Locale.ROOT);
            T value = registry.getValue(new ResourceLocation(NaturesAura.MOD_ID, location));
            try {
                entry.set(null, value);
            } catch (IllegalAccessException e) {
                NaturesAura.LOGGER.error(e);
            }
        }
    }

    public static Block getWool(DyeColor color) {
        switch (color) {
            case ORANGE:
                return Blocks.ORANGE_WOOL;
            case MAGENTA:
                return Blocks.MAGENTA_WOOL;
            case LIGHT_BLUE:
                return Blocks.LIGHT_BLUE_WOOL;
            case YELLOW:
                return Blocks.YELLOW_WOOL;
            case LIME:
                return Blocks.LIME_WOOL;
            case PINK:
                return Blocks.PINK_WOOL;
            case GRAY:
                return Blocks.GRAY_WOOL;
            case LIGHT_GRAY:
                return Blocks.LIGHT_GRAY_WOOL;
            case CYAN:
                return Blocks.CYAN_WOOL;
            case PURPLE:
                return Blocks.PURPLE_WOOL;
            case BLUE:
                return Blocks.BLUE_WOOL;
            case BROWN:
                return Blocks.BROWN_WOOL;
            case GREEN:
                return Blocks.GREEN_WOOL;
            case RED:
                return Blocks.RED_WOOL;
            case BLACK:
                return Blocks.BLACK_WOOL;
            default:
                return Blocks.WHITE_WOOL;
        }
    }
}
