package com.cape39.spigotcape.cape;

import com.cape39.spigotcape.util.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CapeDisplayManager {

    private static final double BACK_OFFSET = 0.30;
    private static final double HEIGHT_OFFSET = 1.15;
    private static final float SCALE = 1.0f;
    private static final float EXTRA_YAW_RADIANS = 0f;

    private final JavaPlugin plugin;
    private final Map<UUID, ItemDisplay> activeDisplays = new HashMap<>();
    private BukkitTask task;

    public CapeDisplayManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 0L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
        removeAll();
    }

    private void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            ItemStack chest = player.getInventory().getChestplate();
            boolean wearingCape = ItemFactory.isCape(chest);

            if (wearingCape) {
                ItemDisplay display = activeDisplays.get(player.getUniqueId());
                if (display == null || display.isDead()) {
                    display = spawnDisplay(player, chest);
                    activeDisplays.put(player.getUniqueId(), display);
                }
                updateDisplayTransform(display, player, chest);
            } else {
                removeDisplayFor(player.getUniqueId());
            }
        }

        Iterator<Map.Entry<UUID, ItemDisplay>> it = activeDisplays.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, ItemDisplay> entry = it.next();
            Player p = plugin.getServer().getPlayer(entry.getKey());
            if (p == null || !p.isOnline()) {
                entry.getValue().remove();
                it.remove();
            }
        }
    }

    private ItemDisplay spawnDisplay(Player player, ItemStack capeStack) {
        Location loc = computeBackLocation(player);
        return player.getWorld().spawn(loc, ItemDisplay.class, entity -> {
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            entity.setBillboard(Display.Billboard.FIXED);
            entity.setPersistent(false);
            entity.setItemStack(ItemFactory.reconstructBannerItemStack(capeStack));
        });
    }

    private void updateDisplayTransform(ItemDisplay display, Player player, ItemStack capeStack) {
        Location loc = computeBackLocation(player);
        display.teleport(loc);

        Vector3f translation = new Vector3f(0f, 0f, 0f);
        Quaternionf leftRotation = new Quaternionf(new AxisAngle4f(EXTRA_YAW_RADIANS, 0f, 1f, 0f));
        Vector3f scale = new Vector3f(SCALE, SCALE, SCALE);
        Quaternionf rightRotation = new Quaternionf();

        display.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));
        display.setItemStack(ItemFactory.reconstructBannerItemStack(capeStack));
    }

    private Location computeBackLocation(Player player) {
        float yaw = player.getLocation().getYaw();

        double radians = Math.toRadians(yaw);
        double backX = Math.sin(radians) * BACK_OFFSET;
        double backZ = -Math.cos(radians) * BACK_OFFSET;

        Location result = player.getLocation().clone();
        result.add(backX, HEIGHT_OFFSET, backZ);
        result.setYaw(yaw);
        result.setPitch(0f);
        return result;
    }

    private void removeDisplayFor(UUID uuid) {
        ItemDisplay display = activeDisplays.remove(uuid);
        if (display != null && !display.isDead()) {
            display.remove();
        }
    }

    private void removeAll() {
        for (ItemDisplay display : activeDisplays.values()) {
            if (!display.isDead()) {
                display.remove();
            }
        }
        activeDisplays.clear();
    }
}
