package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemPickaxe extends PickaxeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemPickaxe(String baseName, IItemTier material, int damage, float speed) {
        super(material, damage, speed, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        if (this == ModItems.INFUSED_IRON_PICKAXE) {
            Player player = context.getPlayer();
            Level level = context.getLevel();
            BlockPos pos = context.getPos();
            ItemStack stack = player.getHeldItem(context.getHand());
            BlockState state = level.getBlockState(pos);
            BlockState result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.get(state);
            if (result != null) {
                if (!level.isClientSide) {
                    level.setBlockState(pos, result);

                    LevelData data = (LevelData) ILevelData.getLevelData(level);
                    data.addMossStone(pos);
                }
                level.playSound(player, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.damageItem(15, player, Player -> Player.sendBreakAnimation(context.getHand()));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (this == ModItems.SKY_PICKAXE) {
            if (!(entityIn instanceof Player))
                return;
            if (!isSelected || levelIn.isClientSide)
                return;
            AxisAlignedBB bounds = new AxisAlignedBB(entityIn.getPosition()).grow(3.5F);
            for (ItemEntity item : levelIn.getEntitiesWithinAABB(ItemEntity.class, bounds)) {
                // only pick up freshly dropped items
                if (item.ticksExisted >= 5 || !item.isAlive())
                    continue;
                item.setPickupDelay(0);
                item.onCollideWithPlayer((Player) entityIn);
            }
        }
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
