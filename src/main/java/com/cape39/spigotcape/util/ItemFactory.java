package com.cape39.spigotcape.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    public static final NamespacedKey FRAME_KEY = new NamespacedKey("cape39", "frame");
    public static final NamespacedKey CAPE_KEY = new NamespacedKey("cape39", "cape");
    public static final NamespacedKey BANNER_MATERIAL_KEY = new NamespacedKey("cape39", "banner_material");
    public static final NamespacedKey BANNER_PATTERNS_KEY = new NamespacedKey("cape39", "banner_patterns");

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

    public static boolean isCape(ItemStack stack) {
        if (stack == null || stack.getType() != Material.LEATHER_CHESTPLATE || !stack.hasItemMeta()) {
            return false;
        }
        return stack.getItemMeta().getPersistentDataContainer().has(CAPE_KEY, PersistentDataType.BYTE);
    }

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
                BANNER_MATERIAL_KEY, PersistentDataType.STRING, bannerStack.getType().name());
        meta.getPersistentDataContainer().set(
                BANNER_PATTERNS_KEY, PersistentDataType.STRING, serializePatterns(patterns));

        cape.setItemMeta(meta);
        return cape;
    }

    public static ItemStack reconstructBannerItemStack(ItemStack capeStack) {
        if (!capeStack.hasItemMeta()) {
            return new ItemStack(Material.WHITE_BANNER);
        }
        var pdc = capeStack.getItemMeta().getPersistentDataContainer();

        String materialName = pdc.getOrDefault(BANNER_MATERIAL_KEY, PersistentDataType.STRING, "WHITE_BANNER");
        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException ex) {
            material = Material.WHITE_BANNER;
        }

        ItemStack banner = new ItemStack(material);
        String patternsData = pdc.getOrDefault(BANNER_PATTERNS_KEY, PersistentDataType.STRING, "");
        List<Pattern> patterns = deserializePatterns(patternsData);

        if (banner.getItemMeta() instanceof BannerMeta bannerMeta) {
            bannerMeta.setPatterns(patterns);
            banner.setItemMeta(bannerMeta);
        }

        return banner;
    }

    private static String serializePatterns(List<Pattern> patterns) {
        StringBuilder sb = new StringBuilder();
        for (Pattern p : patterns) {
            if (sb.length() > 0) sb.append(';');
            sb.append(p.getColor().name()).append(':').append(p.getPattern().name());
        }
        return sb.toString();
    }

    private static List<Pattern> deserializePatterns(String data) {
        List<Pattern> patterns = new ArrayList<>();
        if (data == null || data.isEmpty()) {
            return patterns;
        }
        for (String entry : data.split(";")) {
            if (entry.isEmpty()) continue;
            String[] parts = entry.split(":");
            if (parts.length != 2) continue;
            try {
                DyeColor color = DyeColor.valueOf(parts[0]);
                PatternType type = PatternType.valueOf(parts[1]);
                patterns.add(new Pattern(color, type));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return patterns;
    }

    private static DyeColor dyeColorFromBannerMaterial(Material material) {
        String name = material.name();
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
