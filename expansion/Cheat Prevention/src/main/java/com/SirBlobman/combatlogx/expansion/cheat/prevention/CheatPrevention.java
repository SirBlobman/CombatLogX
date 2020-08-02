package com.SirBlobman.combatlogx.expansion.cheat.prevention;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.listener.*;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CheatPrevention extends Expansion {
    public CheatPrevention(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("cheat-prevention.yml");
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        // All Versions
        manager.registerEvents(new ListenerBlocks(this), plugin);
        manager.registerEvents(new ListenerChat(this), plugin);
        manager.registerEvents(new ListenerCommandBlocker(this), plugin);
        manager.registerEvents(new ListenerEntities(this), plugin);
        manager.registerEvents(new ListenerFlight(this), plugin);
        manager.registerEvents(new ListenerGameMode(this), plugin);
        manager.registerEvents(new ListenerInventories(this), plugin);
        manager.registerEvents(new ListenerPotions(this), plugin);
        manager.registerEvents(new ListenerTeleport(this), plugin);

        int minorVersion = VersionUtil.getMinorVersion();

        // 1.9+ Elytra
        if(minorVersion >= 9) manager.registerEvents(new ListenerElytra(this), plugin);

        // 1.11+ Totem of Undying
        if(minorVersion >= 11) manager.registerEvents(new ListenerTotemOfUndying(this), plugin);

        // 1.12+ PlayerPickupItemEvent --> EntityPickupItemEvent
        Listener itemPickupListener = minorVersion >= 12 ? new ListenerNewItemPickup(this) : new ListenerLegacyItemPickup(this);
        manager.registerEvents(itemPickupListener, plugin);

        // 1.13+ Riptide Enchantment
        if(minorVersion >= 13) manager.registerEvents(new ListenerRiptide(this), plugin);
        
        // Essentials Hook
        if(manager.isPluginEnabled("Essentials")) {
            ListenerEssentials listenerEssentials = new ListenerEssentials(this);
            manager.registerEvents(listenerEssentials, plugin);
        }
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        reloadConfig("cheat-prevention.yml");
    }
}