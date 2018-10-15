package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeRitualHandler {

    public static final BlockPos[] GOLD_POWDER_POSITIONS = new BlockPos[]{
            new BlockPos(-2, 0, 0),
            new BlockPos(2, 0, 0),
            new BlockPos(0, 0, -2),
            new BlockPos(0, 0, 2),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(2, 0, -1),
            new BlockPos(2, 0, 1),
            new BlockPos(-2, 0, -1),
            new BlockPos(-2, 0, 1),
            new BlockPos(1, 0, 2),
            new BlockPos(-1, 0, 2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, -2)
    };
    private static final List<ActiveRitual> ACTIVE_RITUALS = new ArrayList<>();

    public TreeRitualHandler() {
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isRemote) {
            if (Helper.checkMultiblock(world, pos, GOLD_POWDER_POSITIONS, ModBlocks.GOLD_POWDER.getDefaultState(), true)) {
                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(4, 0, 4));
                List<ItemStack> usableItems = new ArrayList<>();
                Set<BlockPos> usedLogs = new HashSet<>();

                for (EntityItem item : items) {
                    BlockPos itemPos = item.getPosition();
                    ItemStack stack = item.getItem();
                    if (stack.getCount() == 1) {
                        if (!usedLogs.contains(itemPos) && world.getBlockState(itemPos.down()).getBlock() instanceof BlockLog) {
                            usedLogs.add(itemPos);
                            usableItems.add(stack);
                        }
                    }
                }

                IBlockState sapling = world.getBlockState(pos);
                ItemStack saplingStack = sapling.getBlock().getItem(world, pos, sapling);
                if (!saplingStack.isEmpty()) {
                    for (TreeRitualRecipe recipe : TreeRitualRecipe.RECIPES) {
                        if (recipe.matchesItems(saplingStack, usableItems)) {
                            ActiveRitual ritual = new ActiveRitual(pos, items, recipe);
                            ACTIVE_RITUALS.add(ritual);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!world.isRemote) {
            for (int i = ACTIVE_RITUALS.size() - 1; i >= 0; i--) {
                ActiveRitual ritual = ACTIVE_RITUALS.get(i);
                if (ritual.isOkay(world)) {
                    ritual.timer++;

                    if (ritual.timer % 3 == 0) {
                        for (EntityItem item : ritual.involvedItems) {
                            PacketHandler.sendToAllAround(world, ritual.pos, 32, new PacketParticleStream(
                                    (float) item.posX, (float) item.posY + 0.5F, (float) item.posZ,
                                    ritual.pos.getX() + 0.5F, ritual.pos.getY() + 1.5F, ritual.pos.getZ() + 0.5F,
                                    world.rand.nextFloat() * 0.05F + 0.05F, 0xFF00FF, world.rand.nextFloat() * 1F + 1F
                            ));
                        }
                    }
                    if (ritual.timer % 5 == 0) {
                        for (BlockPos offset : GOLD_POWDER_POSITIONS) {
                            BlockPos dustPos = ritual.pos.add(offset);
                            PacketHandler.sendToAllAround(world, ritual.pos, 32,
                                    new PacketParticles(
                                            (float) dustPos.getX() + 0.375F + world.rand.nextFloat() * 0.25F,
                                            (float) dustPos.getY() + 0.1F,
                                            (float) dustPos.getZ() + 0.375F + world.rand.nextFloat() * 0.25F,
                                            (float) world.rand.nextGaussian() * 0.01F,
                                            world.rand.nextFloat() * 0.005F + 0.01F,
                                            (float) world.rand.nextGaussian() * 0.01F,
                                            0xf4cb42, 2F, 100, 0F, false, true
                                    ));
                        }
                    }

                    if (ritual.timer >= ritual.recipe.time) {
                        ACTIVE_RITUALS.remove(i);
                    } else if (ritual.timer >= ritual.recipe.time / 2) {
                        for (EntityItem item : ritual.involvedItems) {
                            for (int j = world.rand.nextInt(20) + 10; j >= 0; j--) {
                                PacketHandler.sendToAllAround(world, ritual.pos, 32, new PacketParticles(
                                        (float) item.posX, (float) item.posY + 0.5F, (float) item.posZ,
                                        (float) world.rand.nextGaussian() * 0.05F, world.rand.nextFloat() * 0.05F, (float) world.rand.nextGaussian() * 0.05F,
                                        0xFF00FF, 1.5F, 50, 0F, false, true));
                            }
                            item.setDead();
                        }
                        ritual.involvedItems.clear();
                    }

                } else {
                    ACTIVE_RITUALS.remove(i);
                }
            }
        }
    }

    public static void clear() {
        ACTIVE_RITUALS.clear();
    }

    private static class ActiveRitual {

        private final BlockPos pos;
        private final List<EntityItem> involvedItems;
        private final TreeRitualRecipe recipe;

        private int timer;

        public ActiveRitual(BlockPos pos, List<EntityItem> involvedItems, TreeRitualRecipe recipe) {
            this.pos = pos;
            this.recipe = recipe;

            this.involvedItems = new ArrayList<>();
            for (EntityItem item : involvedItems) {
                if (Helper.containsItem(this.recipe.items, item.getItem())) {
                    this.involvedItems.add(item);
                }
            }
        }

        private boolean isOkay(World world) {
            for (EntityItem item : this.involvedItems) {
                if (item.isDead || item.prevPosX != item.posX || item.prevPosY != item.posY || item.prevPosZ != item.posZ) {
                    return false;
                }
            }
            return Helper.checkMultiblock(world, this.pos, GOLD_POWDER_POSITIONS, ModBlocks.GOLD_POWDER.getDefaultState(), true);
        }
    }
}
