package com.cape39.spigotcape.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Recipes {

    public static final NamespacedKey FRAME_RECIPE_KEY = new NamespacedKey("cape39", "cape_frame");
    public static final NamespacedKey CAPE_RECIPE_KEY = new NamespacedKey("cape39", "banner_cape");

    public static void registerAll(JavaPlugin plugin) {
        registerFrameRecipe(plugin);
        registerCapeRecipe(plugin);
    }

    /**
     * 2 demir külçesi + ortada 1 çubuk (yan yana, tek sıra) -> Cape Frame
     */
    private static void registerFrameRecipe(JavaPlugin plugin) {
        ShapedRecipe recipe = new ShapedRecipe(FRAME_RECIPE_KEY, ItemFactory.createFrame());
        recipe.shape("ISI");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(recipe);
    }

    /**
     * Cape Frame + herhangi bir banner -> Banner Cape.
     *
     * Bukkit'in ShapelessRecipe'i sabit bir "result" item'ı ile kayıt olmak
     * zorunda (crafting kitabında görünmesi için), ama gerçek/doğru renkli
     * sonucu CraftListener (PrepareItemCraftEvent) içinde biz elle
     * hesaplayıp değiştiriyoruz. Bu yüzden buradaki result sadece bir
     * "placeholder" (yer tutucu) - beyaz banner ile önizleme.
     */
    private static void registerCapeRecipe(JavaPlugin plugin) {
        ShapelessRecipe recipe = new ShapelessRecipe(CAPE_RECIPE_KEY,
                ItemFactory.createCapeFromBanner(new ItemStack(Material.WHITE_BANNER)));

        recipe.addIngredient(new RecipeChoice.ExactChoice(ItemFactory.createFrame()));
        recipe.addIngredient(allBannersChoice());

        plugin.getServer().addRecipe(recipe);
    }

    private static RecipeChoice allBannersChoice() {
        List<ItemStack> banners = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.name().endsWith("_BANNER")) {
                banners.add(new ItemStack(m));
            }
        }
        return new RecipeChoice.ExactChoice(banners);
    }
}
