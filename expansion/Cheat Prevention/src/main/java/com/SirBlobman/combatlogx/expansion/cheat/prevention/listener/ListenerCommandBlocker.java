package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import com.SirBlobman.combatlogx.api.ICombatLogX;
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
    public ListenerCommandBlocker(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        String command = e.getMessage();
        String actualCommand = convertCommand(command);
        if(isAllowed(actualCommand)) return;

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

    private boolean isAllowed(String command) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        boolean isWhitelist = config.getBoolean("commands-blocker.whitelist-mode");

        List<String> commandList = config.getStringList("command-blocker.command-list");
        boolean contains = containsMatch(commandList, command);

        /* Return Value Explanation:
        whitelist, contains true: command allowed (true == true returns true)
        whitelist, contains false: command blocked (true == false returns false)
        blacklist, contains true: command blocked (false == true returns false)
        blacklist, contains false: command allowed (false == false returns true)
         */
        return (isWhitelist == contains);
    }

    /**
     * Check if any of the items in the list are the same as or start with the query (case is ignored)
     * @param list The list of items
     * @param query The value to check
     * @return true if a match was found, false otherwise
     */
    private boolean containsMatch(List<String> list, String query) {
        return (containsIgnoreCase(list, query) || startsWithAnyIgnoreCase(list, query));
    }

    private boolean containsIgnoreCase(List<String> list, String query) {
        if(list == null || query == null || query.isEmpty() || list.isEmpty()) return false;

        return list.stream().anyMatch(query::equalsIgnoreCase);
    }

    private boolean startsWithAnyIgnoreCase(List<String> list, String query) {
        if(list == null || query == null || query.isEmpty() || list.isEmpty()) return false;

        String lowerQuery = query.toLowerCase();
        for(String value : list) {
            if(value == null || value.isEmpty()) continue;

            String lowerValue = value.toLowerCase();
            if(lowerQuery.startsWith(lowerValue)) return true;
        }

        return false;
    }
}