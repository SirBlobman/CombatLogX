package com.SirBlobman.combatlogx.expansion.notifier.manager;

import java.util.*;

import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.expansion.notifier.scoreboard.CustomScoreBoard;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreBoardManager {
    private final Notifier expansion;
    private final Map<UUID, CustomScoreBoard> customScoreBoardMap;
    private final Map<UUID, Scoreboard> previousScoreboardMap;
    private final List<UUID> disabledList;
    public ScoreBoardManager(Notifier expansion) {
        this.expansion = expansion;
        this.customScoreBoardMap = new HashMap<>();
        this.previousScoreboardMap = new HashMap<>();
        this.disabledList = new ArrayList<>();
    }


    /**
     * @param player The player to toggle the scoreboard for
     * @return true if the scoreboard was toggled ON, false if the scoreboard was toggled OFF
     */
    public boolean toggleScoreboard(Player player) {
        if(player == null) return false;
        
        UUID uuid = player.getUniqueId();
        if(this.disabledList.contains(uuid)) {
            this.disabledList.remove(uuid);
            return true;
        }
    
        removeScoreboard(player);
        this.disabledList.add(uuid);
        return false;
    }
    
    public boolean isDisabled(Player player) {
        if(player == null) return true;
        
        UUID uuid = player.getUniqueId();
        return this.disabledList.contains(uuid);
    }
    
    public void updateScoreboard(Player player) {
        if(isDisabledGlobally() || isDisabled(player)) {
            removeScoreboard(player);
            return;
        }
        
        UUID uuid = player.getUniqueId();
        CustomScoreBoard customScoreBoard = this.customScoreBoardMap.get(uuid);
        if(customScoreBoard == null) {
            enableScoreboard(player);
            customScoreBoard = this.customScoreBoardMap.get(uuid);
            if(customScoreBoard == null) return;
        }
        
        customScoreBoard.enableScoreboard();
        customScoreBoard.updateScoreboard();
    }
    
    public void removeScoreboard(Player player) {
        UUID uuid = player.getUniqueId();
        CustomScoreBoard customScoreBoard = this.customScoreBoardMap.remove(uuid);
        if(customScoreBoard == null) return;
        
        customScoreBoard.disableScoreboard();
        if(shouldSavePrevious() && this.previousScoreboardMap.containsKey(uuid)) {
            Scoreboard previousScoreboard = this.previousScoreboardMap.remove(uuid);
            if(previousScoreboard == null) return;
            
            Objective objective = previousScoreboard.getObjective("combatlogx");
            if(objective != null) return;
            
            player.setScoreboard(previousScoreboard);
        }
    }
    
    private void enableScoreboard(Player player) {
        if(isDisabledGlobally()) return;
        if(isDisabled(player)) return;
        
        UUID uuid = player.getUniqueId();
        if(shouldSavePrevious() && !this.previousScoreboardMap.containsKey(uuid)) {
            Scoreboard previousScoreboard = player.getScoreboard();
            Objective objective = previousScoreboard.getObjective("combatlogx");
            if(objective == null) this.previousScoreboardMap.put(uuid, previousScoreboard);
        }
        
        CustomScoreBoard customScoreBoard = this.customScoreBoardMap.getOrDefault(uuid, new CustomScoreBoard(this.expansion, player));
        this.customScoreBoardMap.put(uuid, customScoreBoard);
        customScoreBoard.enableScoreboard();
    }
    
    private boolean isDisabledGlobally() {
        FileConfiguration config = this.expansion.getConfig("scoreboard.yml");
        return !config.getBoolean("enabled");
    }
    
    private boolean shouldSavePrevious() {
        FileConfiguration config = this.expansion.getConfig("scoreboard.yml");
        return config.getBoolean("save-previous");
    }
}