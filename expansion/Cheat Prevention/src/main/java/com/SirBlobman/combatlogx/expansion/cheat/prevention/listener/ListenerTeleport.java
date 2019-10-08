package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ListenerTeleport implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerTeleport(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    private boolean isAllowed(PlayerTeleportEvent.TeleportCause cause) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        List<String> allowedCauseList = config.getStringList("teleportation.allowed-cause-list");

        String causeName = cause.name();
        return allowedCauseList.contains(causeName);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("teleportation.prevent-teleport")) return;

        Player player = e.getPlayer();
        ICombatManager manager = this.plugin.getCombatManager();
        if(!manager.isInCombat(player)) return;

        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if(isAllowed(cause)) {
            if(cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && config.getBoolean("teleportation.restart-timer-for-ender-pearl")) {
                manager.tag(player, null, PlayerPreTagEvent.TagType.UNKNOWN, PlayerPreTagEvent.TagReason.UNKNOWN);
            }
            return;
        }

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.teleportation.block-" + (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ? "pearl" : "other"));
        this.plugin.sendMessage(player, message);
    }
}