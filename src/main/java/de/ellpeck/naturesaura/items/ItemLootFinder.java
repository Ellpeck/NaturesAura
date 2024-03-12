package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ItemLootFinder extends ItemImpl {

    public ItemLootFinder() {
        super("loot_finder");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        var inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 100000, false))
            return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
        if (levelIn.isClientSide) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            inst.setParticleCulling(false);

            var pos = playerIn.blockPosition();
            Helper.getBlockEntitiesInArea(levelIn, pos, 64, tile -> {
                if (tile.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, null) != null || tile instanceof SpawnerBlockEntity || BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tile.getType()).getNamespace().equals("lootr")) {
                    inst.spawnMagicParticle(
                            tile.getBlockPos().getX() + 0.5F, tile.getBlockPos().getY() + 0.5F, tile.getBlockPos().getZ() + 0.5F,
                            0F, 0F, 0F, 0xf5f10a, 6F, 20 * 60, 0F, false, true);
                }
                return false;
            });
            for (var entity : levelIn.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(64))) {
                if (!(entity instanceof LivingEntity) && entity.getCapability(Capabilities.ItemHandler.ENTITY) != null) {
                    inst.spawnMagicParticle(
                            entity.getX(), entity.getEyeY(), entity.getZ(),
                            0F, 0F, 0F, 0xf5f10a, 6F, 20 * 60, 0F, false, true);
                }
            }

            inst.setParticleDepth(true);
            inst.setParticleSpawnRange(32);
            inst.setParticleCulling(true);

            playerIn.swing(handIn);
        }
        playerIn.getCooldowns().addCooldown(this, 20 * 60);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

}
