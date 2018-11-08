package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.AuraType;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemAuraBottle extends ItemImpl {

    public ItemAuraBottle() {
        super("aura_bottle");
        this.addPropertyOverride(new ResourceLocation(NaturesAura.MOD_ID, "type"),
                (stack, worldIn, entityIn) -> getType(stack).ordinal());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || held.getItem() != Items.GLASS_BOTTLE)
            return;
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult ray = this.rayTrace(player.world, player, true);
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK)
            return;
        BlockPos pos = player.getPosition();
        if (AuraChunk.getAuraInArea(player.world, pos, 30) < 1000)
            return;

        if (!player.world.isRemote) {
            held.shrink(1);

            player.inventory.addItemStackToInventory(
                    setType(new ItemStack(this), AuraType.forWorld(player.world)));

            BlockPos spot = AuraChunk.getHighestSpot(player.world, pos, 30, pos);
            AuraChunk.getAuraChunk(player.world, spot).drainAura(spot, 200);

            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.PLAYERS, 1F, 1F);
        }
        player.swingArm(event.getHand());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (AuraType type : AuraType.values()) {
                ItemStack stack = new ItemStack(this);
                setType(stack, type);
                items.add(stack);
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + "." + getType(stack).name().toLowerCase() + ".name").trim();
    }

    public static AuraType getType(ItemStack stack) {
        if (!stack.hasTagCompound())
            return AuraType.OTHER;
        String type = stack.getTagCompound().getString("type");
        if (type.isEmpty())
            return AuraType.OTHER;
        return AuraType.valueOf(type);
    }

    public static ItemStack setType(ItemStack stack, AuraType type) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("type", type.name());
        return stack;
    }
}
