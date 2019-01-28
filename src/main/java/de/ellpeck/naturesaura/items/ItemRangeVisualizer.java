package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class ItemRangeVisualizer extends ItemImpl {

    public static final Set<BlockPos> VISUALIZED_BLOCKS = new HashSet<>();
    public static final Set<Entity> VISUALIZED_ENTITIES = new HashSet<>();

    public ItemRangeVisualizer() {
        super("range_visualizer");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IVisualizable) {
            if (worldIn.isRemote)
                if (VISUALIZED_BLOCKS.contains(pos))
                    VISUALIZED_BLOCKS.remove(pos);
                else
                    VISUALIZED_BLOCKS.add(pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() != this)
            return;
        Entity entity = event.getTarget();
        if (entity instanceof IVisualizable) {
            if (entity.world.isRemote) {
                if (VISUALIZED_ENTITIES.contains(entity))
                    VISUALIZED_ENTITIES.remove(entity);
                else
                    VISUALIZED_ENTITIES.add(entity);
            }
            event.getEntityPlayer().swingArm(event.getHand());
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
