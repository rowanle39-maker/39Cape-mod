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

    private static double backOffset = 0.30;
    private static double heightOffset = 1.20;
    private static float scale = 0.8f;
    private static float extraYawRadians = (float) Math.PI;

    private final JavaPlugin plugin;
    private final Map<UUID, ItemStack> wearing = new HashMap<>();
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

    public boolean toggleWear(Player player, ItemStack capeStack) {
        UUID uuid = player.getUniqueId();
        if (wearing.containsKey(uuid)) {
            wearing.remove(uuid);
            removeDisplayFor(uuid);
            return false;
        } else {
            wearing.put(uuid, capeStack.clone());
            return true;
        }
    }

    private void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            ItemStack capeStack = wearing.get(player.getUniqueId());

            if (capeStack != null) {
                ItemDisplay display = activeDisplays.get(player.getUniqueId());
                if (display == null || display.isDead()) {
                    display = spawnDisplay(player, capeStack);
                    activeDisplays.put(player.getUniqueId(), display);
                }
                updateDisplayTransform(display, player, capeStack);
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
                wearing.remove(entry.getKey());
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
        Quaternionf leftRotation = new Quaternionf(new AxisAngle4f(extraYawRadians, 0f, 1f, 0f));
        Vector3f scaleVec = new Vector3f(scale, scale, scale);
        Quaternionf rightRotation = new Quaternionf();

        display.setTransformation(new Transformation(translation, leftRotation, scaleVec, rightRotation));
        display.setItemStack(ItemFactory.reconstructBannerItemStack(capeStack));
    }

    private Location computeBackLocation(Player player) {
        float yaw = player.getLocation().getYaw();

        double radians = Math.toRadians(yaw);
        double backX = Math.sin(radians) * backOffset;
        double backZ = -Math.cos(radians) * backOffset;

        Location result = player.getLocation().clone();
        result.add(backX, heightOffset, backZ);
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
        wearing.clear();
    }
}
