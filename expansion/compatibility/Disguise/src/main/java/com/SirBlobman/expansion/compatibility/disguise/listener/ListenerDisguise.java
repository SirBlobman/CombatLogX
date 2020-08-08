package com.SirBlobman.expansion.compatibility.disguise.listener;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.expansion.compatibility.disguise.CompatibilityDisguise;

public class ListenerDisguise implements Listener {
    private final CompatibilityDisguise expansion;
    public ListenerDisguise(CompatibilityDisguise expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if(!removeLibDisuise(player) && !removeiDisguise(player)) return;

        ILanguageManager languageManager = this.expansion.getPlugin().getLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("disguise-compatibility-remove-disguise");
        languageManager.sendMessage(player, message);
    }

    private boolean removeLibDisuise(Player player) {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("LibsDisguises")) return false;

        me.libraryaddict.disguise.disguisetypes.Disguise disguise = me.libraryaddict.disguise.DisguiseAPI.getDisguise(player);
        return (disguise != null && disguise.removeDisguise());
    }

    private boolean removeiDisguise(Player player) {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("iDisguise")) return false;

        de.robingrether.idisguise.api.DisguiseAPI api = de.robingrether.idisguise.iDisguise.getInstance().getAPI();
        return api.undisguise(player);
    }
}