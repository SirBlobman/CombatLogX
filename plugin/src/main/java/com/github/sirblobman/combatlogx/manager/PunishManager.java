package com.github.sirblobman.combatlogx.manager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.listener.IDeathListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.api.utility.CommandHelper;

public final class PunishManager implements IPunishManager {
    private final ICombatLogX plugin;
    
    public PunishManager(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }
    
    @Override
    public boolean punish(Player player, UntagReason punishReason, LivingEntity previousEnemy) {
        PlayerPunishEvent punishEvent = new PlayerPunishEvent(player, punishReason, previousEnemy);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(punishEvent);
        
        if(!punishEvent.isCancelled()) {
            increasePunishmentCount(player);
            runKillCheck(player);
            runPunishCommands(player, previousEnemy);
            return true;
        }
        
        return false;
    }
    
    @Override
    public long getPunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if(!configuration.getBoolean("enable-punishment-counter")) {
            return 0L;
        }
        
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        return playerData.getLong("punishment-count", 0L);
    }
    
    private void increasePunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if(!configuration.getBoolean("enable-punishment-counter")) {
            return;
        }
        
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        
        long currentCount = playerData.getLong("punishment-count", 0L);
        playerData.set("punishment-count", currentCount + 1L);
        playerDataManager.save(player);
    }
    
    private void runKillCheck(Player player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        String killOptionString = configuration.getString("kill-time");
        if(killOptionString == null) killOptionString = "QUIT";
        
        if(killOptionString.equals("QUIT")) {
            IDeathListener listenerDeath = this.plugin.getDeathListener();
            listenerDeath.add(player);
            player.setHealth(0.0D);
        }
        
        if(killOptionString.equals("JOIN")) {
            YamlConfiguration playerData = this.plugin.getData(player);
            playerData.set("kill-on-join", true);
            this.plugin.saveData(player);
        }
    }
    
    private void runPunishCommands(Player player, LivingEntity previousEnemy) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("commands.yml");
        List<String> punishCommandList = configuration.getStringList("punish-command-list");
        if(punishCommandList.isEmpty()) return;
        
        ICombatManager combatManager = this.plugin.getCombatManager();
        for(String punishCommand : punishCommandList) {
            String replacedCommand = combatManager.replaceVariables(player, previousEnemy, punishCommand);
            if(replacedCommand.startsWith("[PLAYER]")) {
                String command = replacedCommand.substring("[PLAYER]".length());
                CommandHelper.runAsPlayer(this.plugin, player, command);
                continue;
            }
            
            if(replacedCommand.startsWith("[OP]")) {
                String command = replacedCommand.substring("[OP]".length());
                CommandHelper.runAsOperator(this.plugin, player, command);
                continue;
            }
            
            CommandHelper.runAsConsole(this.plugin, replacedCommand);
        }
    }
}
