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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemPickaxe extends PickaxeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemPickaxe(String baseName, Tier material, int damage, float speed) {
        super(material, damage, speed, new Properties().tab(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (this == ModItems.INFUSED_IRON_PICKAXE) {
            var player = context.getPlayer();
            var level = context.getLevel();
            var pos = context.getClickedPos();
            var stack = player.getItemInHand(context.getHand());
            var state = level.getBlockState(pos);
            var result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.get(state);
            if (result != null) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, result);

                    var data = (LevelData) ILevelData.getLevelData(level);
                    data.addMossStone(pos);
                }
                level.playSound(player, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.hurtAndBreak(15, player, p -> p.broadcastBreakEvent(context.getHand()));
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
            var bounds = new AABB(entityIn.blockPosition()).inflate(3.5F);
            for (var item : levelIn.getEntitiesOfClass(ItemEntity.class, bounds)) {
                // only pick up freshly dropped items
                if (item.tickCount >= 5 || !item.isAlive())
                    continue;
                item.setPickUpDelay(0);
                item.playerTouch((Player) entityIn);
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
