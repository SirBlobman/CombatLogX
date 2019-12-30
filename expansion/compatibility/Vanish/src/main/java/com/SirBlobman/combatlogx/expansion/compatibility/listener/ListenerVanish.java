package com.SirBlobman.combatlogx.expansion.compatibility.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.CompatibilityVanish;
import com.SirBlobman.combatlogx.expansion.compatibility.hook.HookBukkitVanish;
import com.SirBlobman.combatlogx.expansion.compatibility.hook.HookEssentials;
import com.SirBlobman.combatlogx.expansion.compatibility.hook.HookSuperVanish;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ListenerVanish implements Listener {
    private final CompatibilityVanish expansion;
    public ListenerVanish(CompatibilityVanish expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        if(canBypassCombat(player)) e.setCancelled(true);
    }

    private boolean canBypassCombat(Player player) {
        FileConfiguration config = this.expansion.getConfig("vanish-compatibility.yml");
        if(!config.getBoolean("bypass-combat")) return false;

        PluginManager manager = Bukkit.getPluginManager();
        if(config.getBoolean("check-essentials-vanish") && manager.isPluginEnabled("Essentials")) {
            return HookEssentials.isVanished(player);
        }

        if(config.getBoolean("check-super-vanish") && (manager.isPluginEnabled("SuperVanish") || manager.isPluginEnabled("PremiumVanish"))) {
            return HookSuperVanish.isVanished(player);
        }

        if(config.getBoolean("check-bukkit-vanish")) {
            return HookBukkitVanish.isVanished(player);
        }

        return false;
    }
}