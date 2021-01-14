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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
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
    public ActionResultType onItemUse(ItemUseContext context) {
        if (this == ModItems.INFUSED_IRON_HOE) {
            World world = context.getWorld();
            ActionResultType result = super.onItemUse(context);
            if (!world.isRemote && result == ActionResultType.SUCCESS) {
                ItemStack seed = ItemStack.EMPTY;
                Random random = world.getRandom();
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
                    ItemEntity item = new ItemEntity(world, pos.getX() + random.nextFloat(), pos.getY() + 1F, pos.getZ() + random.nextFloat(), seed);
                    world.addEntity(item);
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
                    success |= super.onItemUse(newContext) == ActionResultType.SUCCESS;
                }
            }
            return success ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        return super.onItemUse(context);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        if (stack.getItem() == ModItems.SKY_HOE) {
            if (!(player.world.getBlockState(pos).getBlock() instanceof BushBlock))
                return false;
            if (!player.world.isRemote) {
                int range = 3;
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        for (int z = -range; z <= range; z++) {
                            if (x == 0 && y == 0 && z == 0)
                                continue;
                            BlockPos offset = pos.add(x, y, z);
                            BlockState otherState = player.world.getBlockState(offset);
                            if (otherState.getBlock() instanceof BushBlock)
                                player.world.destroyBlock(offset, true);
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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return Helper.makeRechargeProvider(stack, true);
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
