package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class Pickaxe extends PickaxeItem implements IModItem, IModelProvider {

    private final String baseName;

    public Pickaxe(String baseName, IItemTier material, int damage, float speed) {
        super(material, damage, speed, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (this == ModItems.INFUSED_IRON_PICKAXE) {
            PlayerEntity player = context.getPlayer();
            World world = context.getWorld();
            BlockPos pos = context.getPos();
            ItemStack stack = player.getHeldItem(context.getHand());
            BlockState state = world.getBlockState(pos);
            BlockState result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.get(state);
            if (result != null) {
                if (!world.isRemote)
                    world.setBlockState(pos, result);
                world.playSound(player, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.damageItem(15, player, playerEntity -> {});
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (this == ModItems.INFUSED_IRON_PICKAXE)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }
}
