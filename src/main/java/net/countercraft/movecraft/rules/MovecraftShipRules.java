package net.countercraft.movecraft.rules;

import net.countercraft.movecraft.craft.CraftType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovecraftShipRules extends JavaPlugin {
    private static MovecraftShipRules instance;
    private Logger logger;
    private HashMap<CraftType, TypeRules> rulesHashMap;

    @Override
    public void onEnable() {
        reloadTypes();

        getServer().getPluginManager().registerEvents(new PilotListener(), this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
        logger = getLogger();
        rulesHashMap = new HashMap<>();
    }

    public static MovecraftShipRules getInstance(){
        return instance;
    }

    @Nullable
    public TypeRules getRulesByType(CraftType t) {
        return rulesHashMap.get(t);
    }

    public void reloadTypes() {
        rulesHashMap.clear();

        //find our rules directory
        File rulesDirectory = new File(getDataFolder() + "/rules");

        //if there is none, make it and disable the plugin
        if(rulesDirectory.mkdirs()) {
            logger.log(Level.INFO, "No rules directory found, creating one and disabling...");
            setEnabled(false);
            return;
        }

        File[] ruleFiles = rulesDirectory.listFiles();
        if (ruleFiles == null || ruleFiles.length == 0) {
            logger.log(Level.INFO, "No files found in rules directory, disabling...");
            setEnabled(false);
            return;
        }

        //files should have type '.rules'
        for(File f : ruleFiles) {
            if (!f.getName().endsWith(".rules"))
                continue;

            try {
                TypeRules rules = new TypeRules(f);
                rulesHashMap.putIfAbsent(rules.getApplicableType(), rules);
            }
            catch (TypeRules.RulesNotFoundException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }
}
