package net.countercraft.movecraft.rules;

import org.bukkit.plugin.java.JavaPlugin;

public class MovecraftShipRules extends JavaPlugin {
    private static MovecraftShipRules instance;

    @Override
    public void onLoad() {
        instance = this;
        TypeRules.register();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PilotListener(), this);
    }

    public static MovecraftShipRules getInstance() {
        return instance;
    }
}
