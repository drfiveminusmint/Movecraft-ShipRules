package net.countercraft.movecraft.rules;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import net.countercraft.movecraft.craft.type.CraftType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MovecraftShipRules extends JavaPlugin {
    private static MovecraftShipRules instance;
    private Map<CraftType, TypeRules> rules;

    @Override
    public void onEnable() {
        reloadTypes();

        getServer().getPluginManager().registerEvents(new PilotListener(), this);
        getServer().getPluginManager().registerEvents(new TypesReloadedListener(), this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
        rules = new HashMap<>();
    }

    public static MovecraftShipRules getInstance() {
        return instance;
    }

    @Nullable
    public TypeRules getRulesByType(CraftType t) {
        return rules.get(t);
    }

    public void reloadTypes() {
        rules.clear();

        // find our rules directory
        File rulesDirectory = new File(getDataFolder() + "/rules");

        // if there is none, make it and disable the plugin
        if (rulesDirectory.mkdirs()) {
            getLogger().log(Level.INFO, "No rules directory found, creating one and disabling...");
            setEnabled(false);
            return;
        }

        File[] ruleFiles = rulesDirectory.listFiles();
        if (ruleFiles == null || ruleFiles.length == 0) {
            getLogger().log(Level.INFO, "No files found in rules directory, disabling...");
            setEnabled(false);
            return;
        }

        // files should have type '.rules'
        for (File f : ruleFiles) {
            if (!f.getName().endsWith(".yml"))
                continue;

            try {
                TypeRules rule = new TypeRules(f);
                rule.getApplicableTypes().forEach(type -> rules.putIfAbsent(type, rule));
            } catch (IllegalArgumentException e) {
                getLogger().log(Level.WARNING, e.getMessage());
            }
        }
    }
}
