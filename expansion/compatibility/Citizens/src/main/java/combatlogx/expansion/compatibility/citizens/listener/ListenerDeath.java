package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;

public final class ListenerDeath extends ExpansionListener {
    private final CitizensExpansion expansion;

    public ListenerDeath(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDeathNPC(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getCombatNPC(npc);
        if(combatNPC == null) return;

        e.setDroppedExp(0);
        e.getDrops().clear();
        checkForDeathMessages(e, combatNPC);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDamageNPC(NPCDamageByEntityEvent e) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("stay-until-no-damage")) return;

        NPC npc = e.getNPC();
        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getCombatNPC(npc);
        if(combatNPC == null) return;

        combatNPC.resetSurvivalTime();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDespawnNPC(NPCDespawnEvent e) {
        DespawnReason despawnReason = e.getReason();
        if(despawnReason == DespawnReason.PENDING_RESPAWN) return;

        NPC npc = e.getNPC();
        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getCombatNPC(npc);
        if(combatNPC == null) return;

        OfflinePlayer offlinePlayer = combatNPC.getOfflineOwner();
        if(despawnReason == DespawnReason.DEATH) combatNpcManager.dropInventory(combatNPC);
        if(despawnReason != DespawnReason.REMOVAL) combatNpcManager.remove(combatNPC);

        YamlConfiguration data = combatNpcManager.getData(offlinePlayer);
        data.set("citizens-compatibility.punish-next-join", true);
        combatNpcManager.saveData(offlinePlayer);

        JavaPlugin plugin = this.expansion.getPlugin().getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, (Runnable) npc::destroy, 1L);
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("citizens.yml");
    }

    private void checkForDeathMessages(NPCDeathEvent e, CombatNPC npc) {
        OfflinePlayer offlineOwner = npc.getOfflineOwner();
        EntityDeathEvent entityDeathEvent = e.getEvent();
        if(!(entityDeathEvent instanceof PlayerDeathEvent)) return;

        PlayerDeathEvent playerDeathEvent =(PlayerDeathEvent) entityDeathEvent;
        String message = playerDeathEvent.getDeathMessage();

        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        YamlConfiguration data = combatNpcManager.getData(offlineOwner);
        data.set("citizens-compatibility.last-death-message", message);
        combatNpcManager.saveData(offlineOwner);
    }
}
