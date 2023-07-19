package me.pranavverma.advancedtech.general.items.commanders.command_hub;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.pranavverma.advancedtech.general.BaseItems;
import me.pranavverma.advancedtech.general.items.solargen.AdvancedSolarGen.AdvancedSolarGen;
import net.guizhanss.guizhanlib.slimefun.machines.TickingMenuBlock;
import me.pranavverma.advancedtech.AdvancedTech;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;


import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;


public class command_hub extends SlimefunItem implements HologramOwner, Listener {

    public static boolean readyToUse = false;
    //Done
    private boolean AdvancedSolarGenFound = false;

    protected static final Map<Location, Object> CACHES = new HashMap<>();


    public command_hub(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        

        addItemHandler(onBreak());
        addItemHandler(onPlace());
        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return true;
            }
            
            @Override
            public void tick(Block block, SlimefunItem slimefunItem, Config config) {
                if (scanForCommandEngine(block)) {
                    updateHologram(block, "&aReady to Use");
                    readyToUse = true;

                } else {
                    updateHologram(block, "&cEngine NOT Found");
                    readyToUse = false;
                } 

                if (scanForAdvancedSolarGen(block)) {
                    AdvancedSolarGenFound = true;
                    
                    
                } else {
                    AdvancedSolarGenFound = false;
                } 
            }
        });

    }

    @Override
    public void preRegister() {
        BlockUseHandler blockUseHandler = this::onBlockRightClick;
        addItemHandler(blockUseHandler);
        
    }
    




    @Nonnull
    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(@Nonnull Block b) {
                removeHologram(b);
            }
        };
    }







    private void onBlockRightClick(PlayerRightClickEvent e) {
        Block block2 = e.getClickedBlock().get();
        if (block2 != null && BlockStorage.check(block2, BaseItems.command_hub_.getItemId())) {
            if (scanForCommandEngine(block2)) {
                openGUI(e.getPlayer());
            }
        }
    }

        private boolean scanForCommandEngine (@Nonnull Block block){
            BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP};

            for (BlockFace face : faces) {
                Block relativeBlock = block.getRelative(face);


                if (BlockStorage.check(relativeBlock, BaseItems.command_engine_.getItemId())) {
                    return true;
                }
            }
            return false;

        }

        private boolean scanForAdvancedSolarGen (@Nonnull Block block){
            BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

            for (BlockFace face : faces) {
                Block relativeBlock = block.getRelative(face);


                if (BlockStorage.check(relativeBlock, BaseItems.advanced_solar_gen.getItemId())) {
                    return true;
                }
            }
            return false;

        }



    

    @Nonnull
    private BlockPlaceHandler onPlace() {

        return new BlockPlaceHandler(true) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                if (scanForCommandEngine(e.getBlock())) {
                    updateHologram(e.getBlock(), "&aReady to Use");
                    readyToUse = true;
                } else {
                    updateHologram(e.getBlock(), "&cEngine NOT Found");
                    readyToUse = false;
                }

                if (scanForAdvancedSolarGen(e.getBlock())) {
                    AdvancedSolarGenFound = true;
                } else {
                    AdvancedSolarGenFound = false;
                } 
            }

        };
    }





 

    private void openGUI(Player player) {
        int counter = 0;
        int i = 0;
        Inventory gui = Bukkit.createInventory(null, 9, "Active Devices");
        ItemStack emptyItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta emptyMeta = emptyItem.getItemMeta();
        emptyMeta.setDisplayName(" ");
        emptyMeta.setCustomModelData(1);
        emptyItem.setItemMeta(emptyMeta);

        for (i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, emptyItem);
        }
    

        ItemStack commandHubItem = new ItemStack(PlayerHead.getItemStack(PlayerSkin.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2NmMTZiYmZlYzZlMGU1YTU4YTk5YjEwYzUyYWNkMDgzZTJhYzg2ZTAwMTNlMDM3ZDZmOTA2MjQzYjYzYTI3OSJ9fX0=")));
        ItemMeta commandHubMeta = commandHubItem.getItemMeta();
        commandHubMeta.setDisplayName(ChatColor.GREEN + "Command Engine: CONNECTED");
        commandHubMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        commandHubItem.setItemMeta(commandHubMeta);
        gui.setItem(counter, commandHubItem);


        if (AdvancedSolarGenFound) {
            counter++;

            ItemStack AdvancedSolarGenItem = new ItemStack(PlayerHead.getItemStack(PlayerSkin.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZkZDllNTg4ZDI0NjFkMmQzZDA1OGNiM2UwYWYyYjNhMzM2NzYwN2FhMTRkMTI0ZWQ5MmE4MzNmMjVmYjExMiJ9fX0=")));
            ItemMeta AdvancedSolarGenMeta = AdvancedSolarGenItem.getItemMeta();
            AdvancedSolarGenMeta.setDisplayName(ChatColor.GREEN + "Advanced Solar Generator: CONNECTED");
            AdvancedSolarGenMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            AdvancedSolarGenItem.setItemMeta(AdvancedSolarGenMeta);
            gui.setItem(counter, AdvancedSolarGenItem);
        } else {
            counter = counter - 1;
        }




        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(AdvancedTech.class));
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (human instanceof Player && inventory != null && inventory.getSize() == 9 && inventory.getItem(0).hasItemMeta() && inventory.getItem(8).getType() == Material.BLACK_STAINED_GLASS && inventory.getItem(7).getType() == Material.BLACK_STAINED_GLASS && inventory.getItem(6).getType() == Material.BLACK_STAINED_GLASS) {
            if (inventory.getItem(0) == null) {
                
            }

            if (inventory.getItem(6) == null) {
                
            }
            
            if (inventory.getItem(7) == null) {
                
            }

            if (inventory.getItem(8) == null) {
                
            }

            
            event.setResult(Result.DENY); // Cancel the click event
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity human = event.getWhoClicked();
        Inventory inventory = event.getInventory();
        
        

        if (human instanceof Player && inventory != null && inventory.getSize() == 9 && inventory.getItem(0).hasItemMeta() && inventory.getItem(8).getType() == Material.BLACK_STAINED_GLASS && inventory.getItem(7).getType() == Material.BLACK_STAINED_GLASS && inventory.getItem(6).getType() == Material.BLACK_STAINED_GLASS) {
            event.setCancelled(true); // Cancel the drag event
        }
    }
}
