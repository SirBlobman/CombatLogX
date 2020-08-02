package com.SirBlobman.combatlogx.expansion.compatibility.vanish.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

public final class HookEssentials {
    public static IEssentials getAPI() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin("Essentials");
        return (IEssentials) plugin;
    }

    public static User getUser(Player player) {
        if(player == null) return null;

        IEssentials api = getAPI();
        if(api == null) return null;

        return api.getUser(player);
    }

    public static boolean isVanished(Player player) {
        User user = getUser(player);
        if(user == null) return false;

        return user.isVanished();
    }
}