package me.pranavverma.advancedtech.general.items.solargen.AdvancedSolarGen;


import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.pranavverma.advancedtech.general.items.commanders.command_hub.command_hub;
import me.pranavverma.advancedtech.general.items.solargen.AdvancedSolarGen.lib.AdvancedSolarGenLib;


public class AdvancedSolarGen extends AdvancedSolarGenLib {

    public AdvancedSolarGen(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int dayenergy, int nightenergy) {
        
        super(itemGroup, dayenergy, nightenergy, item, recipeType, recipe);
        

    }

    

}
