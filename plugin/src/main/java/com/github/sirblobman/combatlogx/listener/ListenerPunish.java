package com.github.sirblobman.combatlogx.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

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
        ICombatLogX plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");

        if(reason == UntagReason.EXPIRE) return configuration.getBoolean("on-expire");
        if(reason == UntagReason.KICK) return configuration.getBoolean("on-kick");
        if(reason == UntagReason.QUIT) return configuration.getBoolean("on-disconnect");
        return false;
    }
}
