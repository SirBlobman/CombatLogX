package com.SirBlobman.combatlogx.expansion.notifier.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.PlayerHandler;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;

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
    
        String barsString = getBarsPlaceholder(expansion, player);
        String message = timerMessageFormat.replace("{bars}", barsString);
        return MessageUtil.color(this.expansion.replacePlaceholders(player, message));
    }
    
    private String getEndedMessage(Player player) {
        FileConfiguration config = this.expansion.getConfig("actionbar.yml");
        String endedMessageFormat = config.getString("message-ended");
        if(endedMessageFormat == null) return "";
        
        return MessageUtil.color(this.expansion.replacePlaceholders(player, endedMessageFormat));
    }
    
    private String getBarsPlaceholder(Notifier expansion, Player player) {
        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager manager = plugin.getCombatManager();
        double timeLeft = manager.getTimerSecondsLeft(player);
        
        FileConfiguration config = expansion.getConfig("actionbar.yml");
        String barsLeftColor = config.getString("bar-options.left-color");
        String barsRightColor = config.getString("bar-options.right-color");
        String barsLeftSymbol = config.getString("bar-options.left-symbol");
        String barsRightSymbol = config.getString("bar-options.right-symbol");
        int scale = config.getInt("bar-options.scale");
        
        double maxTimer = getMaxTimer(player);
        double progress = (timeLeft / maxTimer);
        long leftBarsCount = Math.round(scale * progress);
        long rightBarsCount = (scale - leftBarsCount);
        
        StringBuilder builder = new StringBuilder(barsLeftColor);
        while(leftBarsCount > 0) {
            leftBarsCount--;
            builder.append(barsLeftSymbol);
        }
        
        if(rightBarsCount > 0) {
            builder.append(barsRightColor);
            while(rightBarsCount > 0) {
                rightBarsCount--;
                builder.append(barsRightSymbol);
            }
        }
        
        String barsString = builder.toString();
        return MessageUtil.color(barsString);
    }
    
    private int getMaxTimer(Player player) {
        ICombatLogX plugin = this.expansion.getPlugin();
        FileConfiguration config = plugin.getConfig("config.yml");
        return config.getInt("combat.timer", 15);
    }
}