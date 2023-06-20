package me.pranavverma.advancedtech.items.diggers.lib.carbonado;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExplosiveTool7x7 extends SimpleSlimefunItem<ToolUseHandler> implements NotPlaceable, DamageableItem {

    private final ItemSetting<Boolean> damageOnUse = new ItemSetting<>(this, "damage-on-use", true);
    private final ItemSetting<Boolean> callExplosionEvent = new ItemSetting<>(this, "call-explosion-event", false);

    public ExplosiveTool7x7(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(damageOnUse, callExplosionEvent);
    }

    @Override
    public ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {
            Player p = e.getPlayer();

            if (!p.isSneaking()) {
                Block b = e.getBlock();

                b.getWorld().createExplosion(b.getLocation(), 0);
                b.getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.2F, 1F);

                List<Block> blocks = findBlocks(b);
                breakBlocks(e, p, tool, b, blocks, drops);
            }
        };
    }

    private void breakBlocks(BlockBreakEvent e, Player p, ItemStack item, Block b, List<Block> blocks, List<ItemStack> drops) {
        List<Block> blocksToDestroy = new ArrayList<>();

        if (callExplosionEvent.getValue()) {
            BlockExplodeEvent blockExplodeEvent = new BlockExplodeEvent(b, blocks, 0);
            Bukkit.getServer().getPluginManager().callEvent(blockExplodeEvent);

            if (!blockExplodeEvent.isCancelled()) {
                for (Block block : blockExplodeEvent.blockList()) {
                    if (canBreak(p, block)) {
                        blocksToDestroy.add(block);
                    }
                }
            }
        } else {
            for (Block block : blocks) {
                if (canBreak(p, block)) {
                    blocksToDestroy.add(block);
                }
            }
        }

        ExplosiveToolBreakBlocksEvent7x7 event = new ExplosiveToolBreakBlocksEvent7x7(p, b, blocksToDestroy, item, this);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (Block block : blocksToDestroy) {
                breakBlock(e, p, item, block, drops);
            }
        }
    }

    private List<Block> findBlocks(Block b) {
        List<Block> blocks = new ArrayList<>(36);

        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(b.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

    @Override
    public boolean isDamageable() {
        return damageOnUse.getValue();
    }

    private boolean canBreak(Player p, Block b) {
        if (b.isEmpty() || b.isLiquid()) {
            return false;
        } else if (SlimefunTag.UNBREAKABLE_MATERIALS.isTagged(b.getType())) {
            return false;
        } else if (!b.getWorld().getWorldBorder().isInside(b.getLocation())) {
            return false;
        } else if (Slimefun.getIntegrations().isCustomBlock(b)) {
            return false;
        } else {
            return Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.BREAK_BLOCK);
        }
    }

    private void breakBlock(BlockBreakEvent e, Player p, ItemStack item, Block b, List<ItemStack> drops) {
        Slimefun.getProtectionManager().logAction(p, b, Interaction.BREAK_BLOCK);
        Material material = b.getType();

        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, material);
        SlimefunItem sfItem = BlockStorage.check(b);

        if (sfItem != null && !sfItem.useVanillaBlockBreaking()) {
            BlockBreakEvent dummyEvent = new BlockBreakEvent(b, e.getPlayer());
            sfItem.callItemHandler(BlockBreakHandler.class, handler -> handler.onPlayerBreak(dummyEvent, item, drops));

            if (!dummyEvent.isCancelled()) {
                drops.addAll(sfItem.getDrops(p));
                b.setType(Material.AIR);
                BlockStorage.clearBlockInfo(b);
            }
        } else {
            b.breakNaturally(item);
        }

        damageItem(p, item);
    }
}
