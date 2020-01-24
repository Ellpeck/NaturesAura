package de.ellpeck.naturesaura.items;

import com.google.common.base.Strings;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.BlockEnderCrate;
import de.ellpeck.naturesaura.gui.ContainerEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class EnderAccess extends ItemImpl {
    public EnderAccess() {
        super("ender_access", new Properties().group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (handIn != Hand.MAIN_HAND)
            return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
        ItemStack stack = playerIn.getHeldItemMainhand();
        String name = BlockEnderCrate.getEnderName(stack);
        if (!Strings.isNullOrEmpty(name)) {
            if (!worldIn.isRemote && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 10000, false)) {
                NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".ender_access", TextFormatting.ITALIC + name + TextFormatting.RESET);
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
                        IItemHandler handler = IWorldData.getOverworldData(inv.player.world).getEnderStorage(name);
                        return new ContainerEnderCrate(ModContainers.ENDER_ACCESS, windowId, player, handler);
                    }
                }, buffer -> buffer.writeString(name));
            }
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        BlockEnderCrate.addEnderNameInfo(stack, tooltip);
    }
}
