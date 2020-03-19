package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ListenerCommandBlocker implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    private final Map<UUID, Long> cooldownMap = new HashMap<>();
    public ListenerCommandBlocker(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = expansion.getPlugin();
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

        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
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
    public void beforeCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player) && !isInCooldown(player)) return;

        String command = e.getMessage();
        String actualCommand = convertCommand(command);
        if(!isBlocked(actualCommand) || isAllowed(actualCommand)) return;

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.command-blocked").replace("{command}", actualCommand);
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void beforeCommand2(PlayerCommandPreprocessEvent e) {
        beforeCommand(e);
    }

    private String convertCommand(String command) {
        if(command == null || command.isEmpty()) return "";
        if(!command.startsWith("/")) command = "/" + command;

        return command;
    }
    
    private boolean isBlocked(String command) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        List<String> blockedCommandList = config.getStringList("command-blocker.blocked-commands");
        return startsWithAny(command, blockedCommandList);
    }
    
    private boolean isAllowed(String command) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        List<String> blockedCommandList = config.getStringList("command-blocker.allowed-commands");
        return startsWithAny(command, blockedCommandList);
    }
    
    private boolean startsWithAny(String command, List<String> commandList) {
        if(commandList.contains("*")) return true;
        if(commandList.contains("/*")) return true;
        
        for(String value : commandList) {
            if(!command.startsWith(value)) continue;
            return true;
        }
        
        return false;
    }
}