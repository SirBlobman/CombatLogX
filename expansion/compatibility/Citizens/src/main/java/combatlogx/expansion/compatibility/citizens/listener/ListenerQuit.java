package combatlogx.expansion.compatibility.citizens.listener;

import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;

public final class ListenerQuit extends CitizensExpansionListener {
    public ListenerQuit(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        if (!isAlwaysSpawnOnQuit()) {
            return;
        }

        Player player = e.getPlayer();
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);

        printDebug("Spawning always-quit NPC for player " + player.getName());
        playerData.set("citizens-compatibility.punish", true);
        combatNpcManager.saveData(player);
        combatNpcManager.createNPC(player, Collections.emptyList());
    }

    private boolean isAlwaysSpawnOnQuit() {
        CitizensConfiguration configuration = getCitizensConfiguration();
        return configuration.isAlwaysSpawnNpcOnQuit();
    }
}
