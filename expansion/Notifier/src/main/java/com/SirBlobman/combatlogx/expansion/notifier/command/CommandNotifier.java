package com.SirBlobman.combatlogx.expansion.notifier.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.expansion.notifier.manager.ActionBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.BossBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.ScoreBoardManager;

public class CommandNotifier implements CommandExecutor {
    private final Notifier expansion;
    public CommandNotifier(Notifier expansion) {
        this.expansion = expansion;
    }
    
    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;
        
        if(!(sender instanceof Player)) {
            ICombatLogX plugin = this.expansion.getPlugin();
            ILanguageManager languageManager = plugin.getLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.not-player");
            languageManager.sendMessage(sender, message);
            return true;
        }
        
        Player player = (Player) sender;
        String sub = args[0].toLowerCase();
        switch(sub) {
            case "bossbar":
            case "boss-bar":
                BossBarManager bossBarManager = this.expansion.getBossBarManager();
                bossBarManager.toggleBossBar(player);
                return true;
                
            case "scoreboard":
            case "score-board":
                ScoreBoardManager scoreBoardManager = this.expansion.getScoreBoardManager();
                scoreBoardManager.toggleScoreboard(player);
                return true;
                
            case "actionbar":
            case "action-bar":
                ActionBarManager actionBarManager = this.expansion.getActionBarManager();
                actionBarManager.toggleActionBar(player);
                return true;
                
            default: break;
        }
        
        player.sendMessage("/notifier boss-bar/score-board/action-bar");
        return true;
    }
}