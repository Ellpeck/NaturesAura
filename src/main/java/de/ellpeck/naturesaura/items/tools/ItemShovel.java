package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemShovel extends ShovelItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemShovel(String baseName, Tier material, float damage, float speed) {
        super(material, damage, speed, new Properties().tab(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        var stack = player.getItemInHand(context.getHand());
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        if (this == ModItems.INFUSED_IRON_SHOVEL) {
            var damage = 0;
            if (state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.MYCELIUM) {
                if (level.getBlockState(pos.above()).getMaterial() == Material.AIR) {
                    level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
                    damage = 5;
                }
            } else {
                var range = player.isShiftKeyDown() ? 0 : 1;
                for (var x = -range; x <= range; x++) {
                    for (var y = -range; y <= range; y++) {
                        var actualPos = pos.offset(x, 0, y);
                        var facing = context.getClickedFace();
                        if (player.mayUseItemAt(actualPos.relative(facing), facing, stack)) {
                            if (facing != Direction.DOWN
                                    && level.getBlockState(actualPos.above()).getMaterial() == Material.AIR
                                    && level.getBlockState(actualPos).getBlock() == Blocks.GRASS_BLOCK) {
                                if (!level.isClientSide)
                                    level.setBlock(actualPos, Blocks.DIRT_PATH.defaultBlockState(), 11);
                                damage = 1;
                            }
                        }
                    }
                }
            }

            if (damage > 0) {
                level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.hurtAndBreak(damage, player, p -> p.broadcastBreakEvent(context.getHand()));
                return InteractionResult.SUCCESS;
            }
        } else if (this == ModItems.SKY_SHOVEL) {
            if (this.getDestroySpeed(stack, state) <= 1)
                return super.useOn(context);
            var otherHand = context.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            var other = player.getItemInHand(otherHand);
            if (other.isEmpty() || !(other.getItem() instanceof BlockItem))
                return super.useOn(context);
            level.removeBlock(pos, false);
            var tile = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            Block.dropResources(state, level, pos, tile, null, ItemStack.EMPTY);
            var newContext = new UseOnContext(player, otherHand, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()));
            other.useOn(newContext);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return Helper.makeRechargeProvider(stack, true);
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
