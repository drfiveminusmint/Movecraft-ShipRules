package net.countercraft.movecraft.rules;

import net.countercraft.movecraft.events.TypesReloadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TypesReloadedListener implements Listener {
    @EventHandler
    public void onTypesReload(TypesReloadedEvent e) {
        MovecraftShipRules.getInstance().reloadTypes();
    }
}
