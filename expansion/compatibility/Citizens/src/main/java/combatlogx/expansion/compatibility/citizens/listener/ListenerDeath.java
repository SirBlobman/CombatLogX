package combatlogx.expansion.compatibility.citizens.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.sirblobman.api.folia.details.RunnableTask;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;

public final class ListenerDeath extends CitizensExpansionListener {
    public ListenerDeath(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDeathNPC(NPCDeathEvent e) {
        printDebug("Detected NPCDeathEvent...");

        NPC npc = e.getNPC();
        CombatNPC combatNPC = getCombatNPC(npc);
        if (combatNPC == null) {
            printDebug("NPC was not a CombatNPC, ignoring event.");
            return;
        }

        printDebug("Setting drops and exp to zero and sending custom death message.");
        e.setDroppedExp(0);
        e.getDrops().clear();
        checkForDeathMessages(e, combatNPC);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageNPC(NPCDamageByEntityEvent e) {
        printDebug("Detected NPCDamageByEntityEvent...");

        CitizensConfiguration configuration = getCitizensConfiguration();
        if (!configuration.isStayUntilNoDamage()) {
            printDebug("Stay until no damage setting is not enabled, ignoring event.");
            return;
        }

        NPC npc = e.getNPC();
        CombatNPC combatNPC = getCombatNPC(npc);
        if (combatNPC == null) {
            printDebug("NPC was not a CombatNPC, ignoring event.");
            return;
        }

        printDebug("Resetting NPC survival time.");
        combatNPC.resetSurvivalTime();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDespawnNPC(NPCDespawnEvent e) {
        printDebug("Detected NPCDespawnEvent...");

        DespawnReason despawnReason = e.getReason();
        if (despawnReason == DespawnReason.PENDING_RESPAWN) {
            printDebug("Despawn reason is PENDING_RESPAWN, ignoring event.");
            return;
        }

        NPC npc = e.getNPC();
        CombatNPC combatNPC = getCombatNPC(npc);
        if (combatNPC == null) {
            printDebug("NPC was not a CombatNPC, ignoring event.");
            return;
        }

        CombatNpcManager combatNpcManager = getCombatNpcManager();
        OfflinePlayer offlinePlayer = combatNPC.getOfflineOwner();
        if (despawnReason == DespawnReason.DEATH) {
            Location location = combatNpcManager.getLocation(npc);
            printDebug("Despawn reason was death, drop NPC inventory.");
            InventoryManager inventoryManager = getInventoryManager();
            inventoryManager.dropInventory(offlinePlayer, location);
        }

        if (despawnReason != DespawnReason.REMOVAL) {
            printDebug("Despawn reason was not removal, destroying NPC.");
            combatNpcManager.remove(combatNPC);

            printDebug("Destroy NPC later.");
            TaskScheduler<ConfigurablePlugin> scheduler = getCombatLogX().getFoliaHelper().getScheduler();
            scheduler.scheduleTask(new RunnableTask<>(getJavaPlugin(), npc::destroy));
        }

        printDebug("Setting player to be punished when they next join.");
        YamlConfiguration data = combatNpcManager.getData(offlinePlayer);
        data.set("citizens-compatibility.punish-next-join", true);
        combatNpcManager.saveData(offlinePlayer);
    }

    private void checkForDeathMessages(@NotNull NPCDeathEvent e, @NotNull CombatNPC npc) {
        OfflinePlayer offlineOwner = npc.getOfflineOwner();
        EntityDeathEvent entityDeathEvent = e.getEvent();
        if (!(entityDeathEvent instanceof PlayerDeathEvent)) {
            return;
        }

        PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) entityDeathEvent;
        String message = playerDeathEvent.getDeathMessage();

        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration data = combatNpcManager.getData(offlineOwner);
        data.set("citizens-compatibility.last-death-message", message);
        combatNpcManager.saveData(offlineOwner);
    }
}
