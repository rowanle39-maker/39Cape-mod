package com.cape39.spigotcape.listeners;

import com.cape39.spigotcape.cape.CapeDisplayManager;
import com.cape39.spigotcape.util.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CapeWearListener implements Listener {

    private final CapeDisplayManager capeDisplayManager;

    public CapeWearListener(CapeDisplayManager capeDisplayManager) {
        this.capeDisplayManager = capeDisplayManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!ItemFactory.isCape(item)) {
            return;
        }

        event.setCancelled(true);
        boolean nowWearing = capeDisplayManager.toggleWear(player, item);

        if (nowWearing) {
            player.sendMessage(ChatColor.GREEN + "Pelerin takıldı");
        } else {
            player.sendMessage(ChatColor.RED + "Pelerin çıkarıldı");
        }
    }
}
