package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.combatlogx.CombatPlugin;
import com.SirBlobman.combatlogx.api.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.api.object.UntagReason;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ListenerPunish extends CombatListener {
    public ListenerPunish(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforePunish(PlayerPunishEvent e) {
        UntagReason untagReason = e.getPunishReason();
        if(checkPunishment(untagReason)) return;
        e.setCancelled(true);
    }

    private boolean checkPunishment(UntagReason reason) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");

        if(reason == UntagReason.EXPIRE) return configuration.getBoolean("on-expire");
        if(reason == UntagReason.KICK) return configuration.getBoolean("on-kick");
        if(reason == UntagReason.QUIT) return configuration.getBoolean("on-disconnect");
        return false;
    }
}