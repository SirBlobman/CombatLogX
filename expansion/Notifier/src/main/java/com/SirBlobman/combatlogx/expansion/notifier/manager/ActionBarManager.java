package com.SirBlobman.combatlogx.expansion.notifier.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.PlayerHandler;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ActionBarManager {
    private final Notifier expansion;
    private final List<UUID> disabledList;
    public ActionBarManager(Notifier expansion) {
        this.expansion = expansion;
        this.disabledList = new ArrayList<>();
    }
    
    public void toggleActionBar(Player player) {
        if(player == null) return;
        
        UUID uuid = player.getUniqueId();
        if(this.disabledList.contains(uuid)) {
            this.disabledList.remove(uuid);
            return;
        }
        
        this.disabledList.add(uuid);
    }
    
    public boolean isDisabled(Player player) {
        if(player == null) return true;
        
        UUID uuid = player.getUniqueId();
        return this.disabledList.contains(uuid);
    }
    
    public void updateActionBar(Player player) {
        if(isDisabledGlobally()) return;
        if(isDisabled(player)) return;
    
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        PlayerHandler playerHandler = nmsHandler.getPlayerHandler();
    
        String message = getTimerMessage(player);
        playerHandler.sendActionBar(player, message);
    }
    
    public void removeActionBar(Player player) {
        if(isDisabledGlobally()) return;
        if(isDisabled(player)) return;
    
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        PlayerHandler playerHandler = nmsHandler.getPlayerHandler();
        
        String message = getEndedMessage(player);
        playerHandler.sendActionBar(player, message);
    }
    
    private boolean isDisabledGlobally() {
        FileConfiguration config = this.expansion.getConfig("actionbar.yml");
        return !config.getBoolean("enabled");
    }
    
    private String getTimerMessage(Player player) {
        FileConfiguration config = this.expansion.getConfig("actionbar.yml");
        String timerMessageFormat = config.getString("message-timer");
        if(timerMessageFormat == null) return "";
    
        String barsLeft = getBarsLeft(expansion, player);
        String barsRight = getBarsRight(expansion, player);
        
        String message = timerMessageFormat.replace("{bars_left}", barsLeft).replace("{bars_right}", barsRight);
        return MessageUtil.color(this.expansion.replacePlaceholders(player, message));
    }
    
    private String getEndedMessage(Player player) {
        FileConfiguration config = this.expansion.getConfig("actionbar.yml");
        String endedMessageFormat = config.getString("message-ended");
        if(endedMessageFormat == null) return "";
        
        return MessageUtil.color(this.expansion.replacePlaceholders(player, endedMessageFormat));
    }
    
    private String getBarsLeft(Notifier expansion, Player player) {
        ICombatLogX plugin = expansion.getPlugin();
        FileConfiguration pluginConfig = plugin.getConfig("config.yml");
        int timer = pluginConfig.getInt("combat.timer");
        
        ICombatManager manager = plugin.getCombatManager();
        int timeLeft = manager.getTimerSecondsLeft(player);
        int right = (timer - timeLeft);
        int left = (timer - right);
        
        FileConfiguration config = expansion.getConfig("actionbar.yml");
        String barsLeftColor = config.getString("bars-left-color");
        StringBuilder builder = new StringBuilder(barsLeftColor == null ? "" : barsLeftColor);
        for(int i = 0; i < left; i++) builder.append("|");
        
        return MessageUtil.color(builder.toString());
    }
    
    private String getBarsRight(Notifier expansion, Player player) {
        ICombatLogX plugin = expansion.getPlugin();
        FileConfiguration pluginConfig = plugin.getConfig("config.yml");
        int timer = pluginConfig.getInt("combat.timer");
        
        ICombatManager manager = plugin.getCombatManager();
        int timeLeft = manager.getTimerSecondsLeft(player);
        int right = (timer - timeLeft);
        
        FileConfiguration config = expansion.getConfig("actionbar.yml");
        String barsRightColor = config.getString("bars-right-color");
        StringBuilder builder = new StringBuilder(barsRightColor == null ? "" : barsRightColor);
        for(int i = 0; i < right; i++) builder.append("|");
    
        return MessageUtil.color(builder.toString());
    }
}