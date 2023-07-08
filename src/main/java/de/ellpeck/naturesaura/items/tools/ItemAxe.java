package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemAxe extends AxeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemAxe(String baseName, Tier material, float damage, float speed) {
        super(material, damage, speed, new Properties());
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.LEAVES)) {
            return this.speed;
        } else {
            return super.getDestroySpeed(stack, state);
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if ((stack.getItem() == ModItems.SKY_AXE || stack.getItem() == ModItems.DEPTH_AXE) && Helper.isToolEnabled(stack) && player.level().getBlockState(pos).is(BlockTags.LOGS)) {
            var horRange = stack.getItem() == ModItems.DEPTH_AXE ? 6 : 1;
            Helper.mineRecursively(player.level(), pos, pos, true, horRange, 32, s -> s.is(BlockTags.LOGS));
            return true;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if ((stack.getItem() == ModItems.SKY_AXE || stack.getItem() == ModItems.DEPTH_AXE) && Helper.toggleToolEnabled(player, stack))
            return InteractionResultHolder.success(stack);
        return super.use(level, player, hand);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return Helper.makeRechargeProvider(stack, true);
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        if (this == ModItems.SKY_AXE || this == ModItems.DEPTH_AXE)
            return;
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
