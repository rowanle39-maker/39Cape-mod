package com.cape39.spigotcape;

import com.cape39.spigotcape.cape.CapeDisplayManager;
import com.cape39.spigotcape.listeners.CapeWearListener;
import com.cape39.spigotcape.listeners.CraftListener;
import com.cape39.spigotcape.util.Recipes;
import org.bukkit.plugin.java.JavaPlugin;

public class CapePlugin extends JavaPlugin {

    private CapeDisplayManager capeDisplayManager;

    @Override
    public void onEnable() {
        Recipes.registerAll(this);
        getServer().getPluginManager().registerEvents(new CraftListener(this), this);

        capeDisplayManager = new CapeDisplayManager(this);
        capeDisplayManager.start();
        getServer().getPluginManager().registerEvents(new CapeWearListener(capeDisplayManager), this);

        getLogger().info("Cape39 aktif edildi.");
    }

    @Override
    public void onDisable() {
        if (capeDisplayManager != null) {
            capeDisplayManager.stop();
        }
        getLogger().info("Cape39 devre dışı bırakıldı.");
    }
}
