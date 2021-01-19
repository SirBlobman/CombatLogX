package com.github.sirblobman.combatlogx.listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.manager.CombatManager;

public class ListenerConfiguration extends CombatListener {
    public ListenerConfiguration(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        if(checkDisabledWorld(player)) {
            e.setCancelled(true);
            return;
        }

        if(checkBypass(player)) {
            e.setCancelled(true);
            return;
        }

        LivingEntity enemy = e.getEnemy();
        if(isSelfCombatDisabled(player, enemy)) {
            e.setCancelled(true);
            return;
        }

        CombatPlugin plugin = getPlugin();
        plugin.printDebug("Finished default beforeTag check without cancellation.");
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        LivingEntity enemy = e.getEnemy();
        runTagCommands(player, enemy);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        checkDeathUntag(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        checkDeathUntag(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        checkEnemyDeathUntag(entity);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onExplode(EntityExplodeEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) entity;
        checkEnemyDeathUntag(livingEntity);
    }

    private boolean checkDisabledWorld(Player player) {
        World world = player.getWorld();
        String worldName = world.getName();

        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        List<String> disabledWorldList = configuration.getStringList("disabled-world-list");
        return disabledWorldList.contains(worldName);
    }

    private boolean checkBypass(Player player) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        String bypassPermissionName = configuration.getString("bypass-permission");
        if(bypassPermissionName == null || bypassPermissionName.isEmpty()) return false;

        Permission bypassPermission = new Permission(bypassPermissionName, "CombatLogX Bypass Permission", PermissionDefault.FALSE);
        return player.hasPermission(bypassPermission);
    }

    private boolean isSelfCombatDisabled(Player player, LivingEntity enemy) {
        if(enemy == null || doesNotEqual(player, enemy)) return false;

        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return !configuration.getBoolean("self-combat");
    }

    private boolean doesNotEqual(Entity entity1, Entity entity2) {
        if(entity1 == entity2) return false;

        UUID uuid1 = entity1.getUniqueId();
        UUID uuid2 = entity2.getUniqueId();
        return !uuid1.equals(uuid2);
    }

    private void checkDeathUntag(Player player) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(!configuration.getBoolean("untag-on-death")) return;

        CombatManager combatManager = plugin.getCombatManager();
        if(combatManager.isInCombat(player)) combatManager.untag(player, UntagReason.SELF_DEATH);
    }

    private void checkEnemyDeathUntag(LivingEntity enemy) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(!configuration.getBoolean("untag-on-death")) return;

        CombatManager combatManager = plugin.getCombatManager();
        OfflinePlayer offlinePlayer = combatManager.getByEnemy(enemy);
        if(offlinePlayer == null || !offlinePlayer.isOnline()) return;

        Player player = offlinePlayer.getPlayer();
        if(player != null && combatManager.isInCombat(player)) combatManager.untag(player, UntagReason.ENEMY_DEATH);
    }

    private void runTagCommands(Player player, LivingEntity enemy) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("commands.yml");

        CombatManager combatManager = plugin.getCombatManager();
        List<String> tagCommandList = configuration.getStringList("tag-command-list");
        if(tagCommandList.isEmpty()) return;

        for(String tagCommand : tagCommandList) {
            String replacedCommand = combatManager.replaceVariables(player, enemy, tagCommand);
            if(replacedCommand.startsWith("[PLAYER]")) {
                String command = replacedCommand.substring("[PLAYER]".length());
                combatManager.runAsPlayer(player, command);
                continue;
            }

            if(replacedCommand.startsWith("[OP]")) {
                String command = replacedCommand.substring("[OP]".length());
                combatManager.runAsOperator(player, command);
                continue;
            }

            combatManager.runAsConsole(replacedCommand);
        }
    }
}