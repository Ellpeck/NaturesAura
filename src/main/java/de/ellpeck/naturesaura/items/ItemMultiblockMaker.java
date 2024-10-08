package de.ellpeck.naturesaura.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemMultiblockMaker extends ItemImpl {

    private static List<IMultiblock> multiblocks;

    public ItemMultiblockMaker() {
        super("multiblock_maker", new Properties().component(Data.TYPE, new Data(0)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (!levelIn.isClientSide && playerIn.isCreative()) {
            var curr = ItemMultiblockMaker.getMultiblockId(stack);
            var next = (curr + 1) % ItemMultiblockMaker.multiblocks().size();
            stack.set(Data.TYPE, new Data(next));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player.isCreative()) {
            var multi = ItemMultiblockMaker.getMultiblock(player.getItemInHand(context.getHand()));
            if (multi == null)
                return InteractionResult.PASS;

            if (!context.getLevel().isClientSide)
                multi.forEach(context.getClickedPos().above(), (char) 0, (blockPos, matcher) -> {
                    context.getLevel().setBlockAndUpdate(blockPos, matcher.defaultState());
                    return true;
                });

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = (MutableComponent) super.getName(stack);
        var multi = ItemMultiblockMaker.getMultiblock(stack);
        return multi == null ? name : name.append(" (" + multi.getName() + ")");
    }

    private static List<IMultiblock> multiblocks() {
        if (ItemMultiblockMaker.multiblocks == null) {
            // some weird mixins call getName way too early, before multiblocks are initialized
            if (NaturesAuraAPI.MULTIBLOCKS.isEmpty())
                return null;
            ItemMultiblockMaker.multiblocks = new ArrayList<>();
            ItemMultiblockMaker.multiblocks.addAll(NaturesAuraAPI.MULTIBLOCKS.values());
        }
        return ItemMultiblockMaker.multiblocks;
    }

    private static int getMultiblockId(ItemStack stack) {
        return stack.get(Data.TYPE).multiblockId();
    }

    private static IMultiblock getMultiblock(ItemStack stack) {
        var multiblocks = ItemMultiblockMaker.multiblocks();
        if (multiblocks == null)
            return null;
        var id = ItemMultiblockMaker.getMultiblockId(stack);
        if (id < 0 || id >= multiblocks.size())
            return null;
        return multiblocks.get(id);
    }

    public record Data(int multiblockId) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("multiblock_id").forGetter(d -> d.multiblockId)
        ).apply(i, Data::new));
        public static final DataComponentType<Data> TYPE = DataComponentType.<Data>builder().persistent(Data.CODEC).cacheEncoding().build();

    }

}
