package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Random;

public class ItemHoe extends HoeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemHoe(String baseName, IItemTier material, int speed) {
        super(material, speed, 0, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        if (this == ModItems.INFUSED_IRON_HOE) {
            Level level = context.getLevel();
            InteractionResult result = super.onItemUse(context);
            if (!level.isClientSide && result.isSuccessOrConsume()) {
                ItemStack seed = ItemStack.EMPTY;
                Random random = level.getRandom();
                BlockPos pos = context.getPos();
                if (random.nextInt(5) == 0) {
                    seed = new ItemStack(Items.WHEAT_SEEDS);
                } else if (random.nextInt(10) == 0) {
                    int rand = random.nextInt(3);
                    if (rand == 0) {
                        seed = new ItemStack(Items.MELON_SEEDS);
                    } else if (rand == 1) {
                        seed = new ItemStack(Items.PUMPKIN_SEEDS);
                    } else if (rand == 2) {
                        seed = new ItemStack(Items.BEETROOT_SEEDS);
                    }
                }

                if (!seed.isEmpty()) {
                    ItemEntity item = new ItemEntity(level, pos.getX() + random.nextFloat(), pos.getY() + 1F, pos.getZ() + random.nextFloat(), seed);
                    level.addEntity(item);
                }
            }
            return result;
        } else if (this == ModItems.SKY_HOE) {
            boolean success = false;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos offset = context.getPos().add(x, 0, z);
                    BlockRayTraceResult newResult = new BlockRayTraceResult(context.getHitVec(), context.getFace(), offset, context.isInside());
                    ItemUseContext newContext = new ItemUseContext(context.getPlayer(), context.getHand(), newResult);
                    success |= super.onItemUse(newContext) == InteractionResult.SUCCESS;
                }
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return super.onItemUse(context);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (stack.getItem() == ModItems.SKY_HOE) {
            if (!(player.level.getBlockState(pos).getBlock() instanceof BushBlock))
                return false;
            if (!player.level.isClientSide) {
                int range = 3;
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        for (int z = -range; z <= range; z++) {
                            if (x == 0 && y == 0 && z == 0)
                                continue;
                            BlockPos offset = pos.add(x, y, z);
                            BlockState otherState = player.level.getBlockState(offset);
                            if (otherState.getBlock() instanceof BushBlock)
                                player.level.destroyBlock(offset, true);
                        }
                    }
                }
            }
        }
        return false;
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
