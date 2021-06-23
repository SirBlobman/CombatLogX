package com.SirBlobman.combatlogx.expansion.notifier.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.MessageUtility;

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
            ILanguageManager languageManager = plugin.getCombatLogXLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.not-player");
            languageManager.sendMessage(sender, message);
            return true;
        }
        
        Player player = (Player) sender;
        ICombatLogX plugin = this.expansion.getPlugin();
        ILanguageManager languageManager = plugin.getCombatLogXLanguageManager();

        String permission = "combatlogx.notifier.toggle";
        if(!sender.hasPermission(permission)) {
            languageManager.sendLocalizedMessage(player, "errors.no-permission", message -> message.replace("{permission}", permission));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch(sub) {
            case "bossbar":
            case "boss-bar":
                BossBarManager bossBarManager = this.expansion.getBossBarManager();
                boolean statusBossBar = bossBarManager.toggleBossBar(player);
                sendToggleMessage(player, "bossbar", statusBossBar);
                return true;
                
            case "scoreboard":
            case "score-board":
                ScoreBoardManager scoreBoardManager = this.expansion.getScoreBoardManager();
                boolean statusScoreBoard = scoreBoardManager.toggleScoreboard(player);
                sendToggleMessage(player, "scoreboard", statusScoreBoard);
                return true;
                
            case "actionbar":
            case "action-bar":
                ActionBarManager actionBarManager = this.expansion.getActionBarManager();
                boolean statusActionBar = actionBarManager.toggleActionBar(player);
                sendToggleMessage(player, "actionbar", statusActionBar);
                return true;
                
            default: break;
        }
        
        player.sendMessage("/notifier boss-bar/score-board/action-bar");
        return true;
    }

    private void sendToggleMessage(Player player, String type, boolean status) {
        ICombatLogX plugin = this.expansion.getPlugin();
        ILanguageManager languageManager = plugin.getCombatLogXLanguageManager();

        String messageFormat = languageManager.getLocalizedMessage(player, "notifier.toggle-" + type);
        if(messageFormat == null) return;

        String statusString = languageManager.getLocalizedMessage(player, "placeholders.status.option-" + (status ? "enabled" : "disabled"));
        if(statusString == null) statusString = Boolean.toString(status);
        String message = messageFormat.replace("{status}", statusString);

        String color = MessageUtility.color(message);
        languageManager.sendMessage(player, color);
    }
}
