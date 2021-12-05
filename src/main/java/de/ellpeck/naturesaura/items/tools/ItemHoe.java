package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Random;

public class ItemHoe extends HoeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemHoe(String baseName, Tier material, int speed) {
        super(material, speed, 0, new Properties().tab(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (this == ModItems.INFUSED_IRON_HOE) {
            Level level = context.getLevel();
            InteractionResult result = super.useOn(context);
            if (!level.isClientSide && result.consumesAction()) {
                ItemStack seed = ItemStack.EMPTY;
                Random random = level.getRandom();
                BlockPos pos = context.getClickedPos();
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
                    level.addFreshEntity(item);
                }
            }
            return result;
        } else if (this == ModItems.SKY_HOE) {
            boolean success = false;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos offset = context.getClickedPos().offset(x, 0, z);
                    BlockHitResult newResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), offset, context.isInside());
                    UseOnContext newContext = new UseOnContext(context.getPlayer(), context.getHand(), newResult);
                    success |= super.useOn(newContext) == InteractionResult.SUCCESS;
                }
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return super.useOn(context);
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
                            BlockPos offset = pos.offset(x, y, z);
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
