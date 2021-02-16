package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerCommandBlocker extends CheatPreventionListener {
    private final Map<UUID, Long> cooldownMap;
    public ListenerCommandBlocker(CheatPrevention expansion) {
        super(expansion);
        this.cooldownMap = new HashMap<>();
    }

    private boolean isInCooldown(Player player) {
        if(player == null) return false;

        UUID uuid = player.getUniqueId();
        long expireTime = cooldownMap.getOrDefault(uuid, 0L);
        long systemTime = System.currentTimeMillis();
        if(systemTime >= expireTime) {
            removeCooldown(player);
            return false;
        }

        return true;
    }

    private void addCooldown(Player player) {
        if(player == null) return;

        FileConfiguration config = getConfig();
        long cooldownSeconds = config.getLong("command-blocker.delay-after-combat");
        if(cooldownSeconds <= 0) return;

        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);
        long systemTime = System.currentTimeMillis();
        long expireTime = (systemTime + cooldownMillis);

        UUID uuid = player.getUniqueId();
        cooldownMap.put(uuid, expireTime);
    }

    private void removeCooldown(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        cooldownMap.remove(uuid);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        UntagReason reason = e.getUntagReason();
        if(!reason.isExpire()) return;

        Player player = e.getPlayer();
        addCooldown(player);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeCommandLowest(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player) && !isInCooldown(player)) return;

        String command = e.getMessage();
        String actualCommand = convertCommand(command).toLowerCase();
        if(!isBlocked(actualCommand) || isAllowed(actualCommand)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.command-blocked").replace("{command}", actualCommand);
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void beforeCommandHigh(PlayerCommandPreprocessEvent e) {
        beforeCommandLowest(e);
    }

    private String convertCommand(String command) {
        if(command == null || command.isEmpty()) return "";
        return (command.startsWith("/") ? command : ("/" + command));
    }
    
    private boolean isBlocked(String command) {
        FileConfiguration config = getConfig();
        List<String> blockedCommandList = config.getStringList("command-blocker.blocked-commands");
        return startsWithAny(command, blockedCommandList);
    }
    
    private boolean isAllowed(String command) {
        FileConfiguration config = getConfig();
        List<String> blockedCommandList = config.getStringList("command-blocker.allowed-commands");
        return startsWithAny(command, blockedCommandList);
    }
    
    private boolean startsWithAny(String command, List<String> commandList) {
        if(commandList.contains("*") || commandList.contains("/*")) return true;
        for(String value : commandList) {
            if(!command.startsWith(value.toLowerCase())) continue;
            return true;
        }
        
        return false;
    }
}