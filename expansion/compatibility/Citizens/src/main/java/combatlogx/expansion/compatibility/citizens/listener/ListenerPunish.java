package combatlogx.expansion.compatibility.citizens.listener;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;

public final class ListenerPunish extends CitizensExpansionListener {
    public ListenerPunish(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforePunish(PlayerPunishEvent e) {
        printDebug("Detected PlayerPunishEvent.");

        CitizensConfiguration configuration = getCitizensConfiguration();
        if (configuration.isPreventPunishments()) {
            printDebug("Cancelling all other CombatLogX punishments.");
            e.setCancelled(true);
        }

        Player player = e.getPlayer();
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);

        printDebug("Spawning NPC for player " + player.getName());
        playerData.set("citizens-compatibility.punish", true);
        combatNpcManager.saveData(player);
        combatNpcManager.createNPC(player, e.getEnemies());
    }
}
