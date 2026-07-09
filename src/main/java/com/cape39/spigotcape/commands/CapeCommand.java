package com.cape39.spigotcape.commands;

import com.cape39.spigotcape.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CapeCommand implements CommandExecutor {

    private static final List<String> VALID_COLORS = Arrays.asList(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /cape39 <renk> <oyuncu_adı>");
            sender.sendMessage(ChatColor.GRAY + "Renkler: " + String.join(", ", VALID_COLORS));
            return true;
        }

        String colorArg = args[0].toLowerCase(Locale.ROOT);
        if (!VALID_COLORS.contains(colorArg)) {
            sender.sendMessage(ChatColor.RED + "Geçersiz renk: " + colorArg);
            sender.sendMessage(ChatColor.GRAY + "Renkler: " + String.join(", ", VALID_COLORS));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Oyuncu bulunamadı: " + args[1]);
            return true;
        }

        Material bannerMaterial = Material.matchMaterial(colorArg.toUpperCase(Locale.ROOT) + "_BANNER");
        if (bannerMaterial == null) {
            sender.sendMessage(ChatColor.RED + "Bu renk için banner materyali bulunamadı: " + colorArg);
            return true;
        }

        ItemStack banner = new ItemStack(bannerMaterial);
        ItemStack cape = ItemFactory.createCapeFromBanner(banner);

        target.getInventory().addItem(cape);
        target.sendMessage(ChatColor.GREEN + "Sana bir " + colorArg + " pelerin verildi! Elinde tutup sağ tıkla.");
        sender.sendMessage(ChatColor.GREEN + target.getName() + " oyuncusuna " + colorArg + " pelerin verildi.");

        return true;
    }
}
