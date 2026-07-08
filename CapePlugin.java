package com.cape39.spigotcape;

import com.cape39.spigotcape.listeners.CraftListener;
import com.cape39.spigotcape.util.Recipes;
import org.bukkit.plugin.java.JavaPlugin;

public class CapePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Recipes.registerAll(this);
        getServer().getPluginManager().registerEvents(new CraftListener(this), this);
        getLogger().info("Cape39 aktif edildi.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cape39 devre dışı bırakıldı.");
    }
}
