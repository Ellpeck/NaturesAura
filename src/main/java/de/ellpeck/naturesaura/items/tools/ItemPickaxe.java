package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public ActionResultType onItemUse(ItemUseContext context) {
        if (this == ModItems.INFUSED_IRON_PICKAXE) {
            PlayerEntity player = context.getPlayer();
            World world = context.getWorld();
            BlockPos pos = context.getPos();
            ItemStack stack = player.getHeldItem(context.getHand());
            BlockState state = world.getBlockState(pos);
            BlockState result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.get(state);
            if (result != null) {
                if (!world.isRemote) {
                    world.setBlockState(pos, result);

                    WorldData data = (WorldData) IWorldData.getWorldData(world);
                    data.addMossStone(pos);
                }
                world.playSound(player, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.damageItem(15, player, playerEntity -> playerEntity.sendBreakAnimation(context.getHand()));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (this == ModItems.SKY_PICKAXE) {
            if (!(entityIn instanceof PlayerEntity))
                return;
            if (!isSelected || worldIn.isRemote)
                return;
            AxisAlignedBB bounds = new AxisAlignedBB(entityIn.getPosition()).grow(3.5F);
            for (ItemEntity item : worldIn.getEntitiesWithinAABB(ItemEntity.class, bounds)) {
                // only pick up freshly dropped items
                if (item.getAge() >= 5)
                    continue;
                item.setPickupDelay(0);
                item.onCollideWithPlayer((PlayerEntity) entityIn);
            }
        }
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
