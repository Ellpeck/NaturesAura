package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ItemHoe extends HoeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemHoe(String baseName, Tier material, int speed) {
        super(material, new Properties().attributes(HoeItem.createAttributes(material, 0, speed)));
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (this == ModItems.INFUSED_IRON_HOE) {
            var level = context.getLevel();
            var result = super.useOn(context);
            if (!level.isClientSide && result.consumesAction()) {
                var seed = ItemStack.EMPTY;
                var random = level.getRandom();
                var pos = context.getClickedPos();
                if (random.nextInt(5) == 0) {
                    seed = new ItemStack(Items.WHEAT_SEEDS);
                } else if (random.nextInt(10) == 0) {
                    var rand = random.nextInt(3);
                    if (rand == 0) {
                        seed = new ItemStack(Items.MELON_SEEDS);
                    } else if (rand == 1) {
                        seed = new ItemStack(Items.PUMPKIN_SEEDS);
                    } else if (rand == 2) {
                        seed = new ItemStack(Items.BEETROOT_SEEDS);
                    }
                }

                if (!seed.isEmpty()) {
                    var item = new ItemEntity(level, pos.getX() + random.nextFloat(), pos.getY() + 1F, pos.getZ() + random.nextFloat(), seed);
                    level.addFreshEntity(item);
                }
            }
            return result;
        } else if (this == ModItems.SKY_HOE) {
            var success = false;
            for (var x = -1; x <= 1; x++) {
                for (var z = -1; z <= 1; z++) {
                    var offset = context.getClickedPos().offset(x, 0, z);
                    var newResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), offset, context.isInside());
                    var newContext = new UseOnContext(context.getPlayer(), context.getHand(), newResult);
                    success |= super.useOn(newContext).consumesAction();
                }
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!miningEntity.isShiftKeyDown() && (stack.getItem() == ModItems.SKY_HOE || stack.getItem() == ModItems.DEPTH_HOE)) {
            var block = level.getBlockState(pos).getBlock();
            if (block instanceof BushBlock || stack.getItem() == ModItems.DEPTH_HOE && block instanceof LeavesBlock) {
                if (!level.isClientSide) {
                    var range = 3;
                    for (var x = -range; x <= range; x++) {
                        for (var y = -range; y <= range; y++) {
                            for (var z = -range; z <= range; z++) {
                                if (x == 0 && y == 0 && z == 0)
                                    continue;
                                var offset = pos.offset(x, y, z);
                                var offState = level.getBlockState(offset);
                                if (offState.getBlock() instanceof BushBlock || stack.getItem() == ModItems.DEPTH_HOE && offState.getBlock() instanceof LeavesBlock) {
                                    var entity = offState.hasBlockEntity() ? level.getBlockEntity(offset) : null;
                                    Block.dropResources(offState, level, offset, entity, null, stack);
                                    level.setBlock(offset, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
