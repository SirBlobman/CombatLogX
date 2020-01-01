package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class ListenerGameMode implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerGameMode(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    private GameMode getGameMode() {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        String gameModeString = config.getString("game-mode.game-mode");

        try {
            return GameMode.valueOf(gameModeString);
        } catch(IllegalArgumentException | NullPointerException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "Invalid GameMode '" + gameModeString + "'. Defaulting to SURVIVAL.");
            return GameMode.SURVIVAL;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("game-mode.prevent-game-mode-change")) return;

        Player player = e.getPlayer();
        ICombatManager manager = this.plugin.getCombatManager();
        if(!manager.isInCombat(player)) return;

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.game-mode.no-changing");
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("game-mode.change-game-mode")) return;

        Player player = e.getPlayer();
        GameMode playerGameMode = player.getGameMode();
        GameMode gameMode = getGameMode();
        if(playerGameMode == gameMode) return;

        player.setGameMode(gameMode);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.game-mode.force-changed").replace("{game_mode}", gameMode.name());
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void afterChangeGameMode(PlayerGameModeChangeEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("game-mode.untag-on-change-game-mode")) return;

        Player player = e.getPlayer();
        ICombatManager manager = this.plugin.getCombatManager();
        if(!manager.isInCombat(player)) return;

        manager.untag(player, UntagReason.EXPIRE);
    }
}