package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerGameMode extends CheatPreventionListener {
    public ListenerGameMode(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("game-mode.prevent-game-mode-change")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.game-mode.no-changing");
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("game-mode.change-game-mode")) return;

        GameMode gameMode = getGameMode();
        String gameModeName = gameMode.name();

        Player player = e.getPlayer();
        GameMode playerGameMode = player.getGameMode();
        if(playerGameMode == gameMode) return;

        player.setGameMode(gameMode);
        String message = getMessage("cheat-prevention.game-mode.force-changed").replace("{game_mode}", gameModeName);
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void afterChangeGameMode(PlayerGameModeChangeEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("game-mode.untag-on-change-game-mode")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        ICombatLogX plugin = getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.untag(player, UntagReason.EXPIRE);
    }

    private GameMode getGameMode() {
        FileConfiguration config = getConfig();
        String gameModeName = config.getString("game-mode.game-mode");
        if(gameModeName == null) return GameMode.SURVIVAL;

        try {
            String gameModeValue = gameModeName.toUpperCase();
            return GameMode.valueOf(gameModeValue);
        } catch(Exception ex) {
            Logger logger = getLogger();
            logger.log(Level.WARNING, "An error occurred while parsing the configured game-mode.", ex);
            logger.log(Level.WARNING, "Defaulting to SURVIVAL.");
            return GameMode.SURVIVAL;
        }
    }
}