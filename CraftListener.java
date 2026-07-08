package com.cape39.spigotcape.listeners;

import com.cape39.spigotcape.util.ItemFactory;
import com.cape39.spigotcape.util.Recipes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftListener implements Listener {

    private final JavaPlugin plugin;

    public CraftListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            return;
        }
        if (!craftingRecipe.getKey().equals(Recipes.CAPE_RECIPE_KEY)) {
            return;
        }

        // Crafting alanındaki banner'ı bul.
        ItemStack bannerStack = null;
        for (ItemStack stack : event.getInventory().getMatrix()) {
            if (ItemFactory.isBanner(stack)) {
                bannerStack = stack;
                break;
            }
        }

        if (bannerStack == null) {
            return; // beklenmedik durum, placeholder sonuç kalsın
        }

        ItemStack correctResult = ItemFactory.createCapeFromBanner(bannerStack);
        event.getInventory().setResult(correctResult);
    }
}
