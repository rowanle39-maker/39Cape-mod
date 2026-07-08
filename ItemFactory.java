package com.cape39.spigotcape.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Cape Frame ve Banner Cape eşyalarını oluşturan yardımcı sınıf.
 *
 * NOT: Spigot/Paper ile resource pack olmadan yeni bir texture/model
 * eklenemez. Bu yüzden:
 *  - Cape Frame: vanilla DEMİR KÜLÇESİ (Iron Ingot) görünümünde ama özel
 *    isim + gizli etiket (PDC) taşıyan bir eşya.
 *  - Banner Cape: vanilla DERİ GÖĞÜSLÜK (Leather Chestplate) baz alınıyor,
 *    çünkü deri zırhın rengi kod ile değiştirilebiliyor. Böylece banner'ın
 *    ana rengi gerçekten pelerine yansıyor (resource pack gerekmeden).
 *    Banner'daki desenler (patterns) ise lore (açıklama) satırlarında
 *    listeleniyor; gerçek görsel desen ancak bir resource pack ile
 *    (custom model data) eklenebilir.
 */
public class ItemFactory {

    public static final NamespacedKey FRAME_KEY = new NamespacedKey("cape39", "frame");
    public static final NamespacedKey CAPE_KEY = new NamespacedKey("cape39", "cape");

    public static ItemStack createFrame() {
        ItemStack stack = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Pelerin Çerçevesi");
        meta.setLore(List.of(ChatColor.DARK_GRAY + "Bir banner ile birleştirerek",
                ChatColor.DARK_GRAY + "pelerin yapabilirsin."));
        meta.getPersistentDataContainer().set(FRAME_KEY, PersistentDataType.BYTE, (byte) 1);
        stack.setItemMeta(meta);
        return stack;
    }

    public static boolean isFrame(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || !stack.hasItemMeta()) {
            return false;
        }
        return stack.getItemMeta().getPersistentDataContainer().has(FRAME_KEY, PersistentDataType.BYTE);
    }

    public static boolean isBanner(ItemStack stack) {
        return stack != null && stack.getType().name().endsWith("_BANNER");
    }

    /**
     * Banner item'ının rengini ve desenlerini okuyup, o veriyle bir
     * Banner Cape (giyilebilir deri göğüslük) üretir.
     */
    public static ItemStack createCapeFromBanner(ItemStack bannerStack) {
        DyeColor baseColor = dyeColorFromBannerMaterial(bannerStack.getType());

        List<Pattern> patterns = new ArrayList<>();
        if (bannerStack.getItemMeta() instanceof BannerMeta bannerMeta) {
            patterns.addAll(bannerMeta.getPatterns());
        }

        ItemStack cape = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) cape.getItemMeta();

        Color color = (baseColor != null) ? baseColor.getColor() : Color.WHITE;
        meta.setColor(color);

        String colorName = (baseColor != null) ? baseColor.name() : "WHITE";
        meta.setDisplayName(ChatColor.WHITE + capitalize(colorName) + " Pelerin");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "39 Cape Mod");
        lore.add(ChatColor.DARK_GRAY + "Ana renk: " + ChatColor.GRAY + capitalize(colorName));
        lore.add(ChatColor.DARK_GRAY + "Desen sayısı: " + ChatColor.GRAY + patterns.size());
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(CAPE_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(
                new NamespacedKey("cape39", "base_color"),
                PersistentDataType.STRING,
                colorName);
        meta.getPersistentDataContainer().set(
                new NamespacedKey("cape39", "pattern_count"),
                PersistentDataType.INTEGER,
                patterns.size());

        cape.setItemMeta(meta);
        return cape;
    }

    private static DyeColor dyeColorFromBannerMaterial(Material material) {
        String name = material.name(); // örn. "RED_BANNER"
        String colorPart = name.substring(0, name.length() - "_BANNER".length());
        try {
            return DyeColor.valueOf(colorPart);
        } catch (IllegalArgumentException ex) {
            return DyeColor.WHITE;
        }
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
