package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagReason;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerTeleport extends CheatPreventionListener {
    public ListenerTeleport(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("teleportation.prevent-teleport")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        TeleportCause teleportCause = e.getCause();
        if(isAllowed(teleportCause)) {
            boolean restartTimer = config.getBoolean("teleportation.restart-timer-for-ender-pearl");
            if(restartTimer && teleportCause == TeleportCause.ENDER_PEARL) {
                ICombatLogX plugin = getPlugin();
                ICombatManager combatManager = plugin.getCombatManager();
                combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
            }

            return;
        }

        e.setCancelled(true);
        String path = (teleportCause == TeleportCause.ENDER_PEARL ? "pearl" : "other");
        String message = getMessage("cheat-prevention.teleportation.block." + path);
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void afterTeleport(PlayerTeleportEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("teleportation.untag-on-teleport")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        ICombatLogX plugin = getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.untag(player, UntagReason.EXPIRE);
    }

    private boolean isAllowed(TeleportCause cause) {
        FileConfiguration config = getConfig();
        List<String> allowedCauseList = config.getStringList("teleportation.allowed-cause-list");

        String causeName = cause.name();
        return allowedCauseList.contains(causeName);
    }
}