package com.github.sirblobman.combatlogx.listener;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.CommandConfiguration;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class ListenerConfiguration extends CombatListener {
    public ListenerConfiguration(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        printDebug("Detected PlayerPreTagEvent.");

        TagReason tagReason = e.getTagReason();
        printDebug("Tag Reason: " + tagReason);
        if (isDisabled(tagReason)) {
            printDebug("Reason disabled by configuration.");
            e.setCancelled(true);
            return;
        }

        Player player = e.getPlayer();
        printDebug("Player: " + player.getName());

        if (isWorldDisabled(player)) {
            printDebug("Player is in disabled world, cancelling.");
            e.setCancelled(true);
            return;
        }

        if (checkBypass(player)) {
            printDebug("Player has bypass, cancelling.");
            e.setCancelled(true);
            return;
        }

        Entity enemy = e.getEnemy();
        if (isSelfCombatDisabled(player, enemy)) {
            printDebug("Self combat is disabled, cancelling.");
            e.setCancelled(true);
            return;
        }

        printDebug("Finished default beforeTag check without cancellation.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        runTagCommands(player, enemy);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        checkDeathUntag(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        checkDeathUntag(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        checkEnemyDeathUntag(entity);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        checkEnemyDeathUntag(livingEntity);
    }

    private @NotNull MainConfiguration getConfiguration() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getConfiguration();
    }

    private boolean checkBypass(@NotNull Player player) {
        ICombatManager combatManager = getCombatManager();
        return combatManager.canBypass(player);
    }

    private boolean isSelfCombatDisabled(@NotNull Player player, @Nullable Entity enemy) {
        MainConfiguration configuration = getConfiguration();
        if (configuration.isSelfCombat()) {
            return false;
        }

        return (enemy != null && isEqual(player, enemy));
    }

    private boolean isEqual(@NotNull Entity entity1, @NotNull Entity entity2) {
        if (entity1 == entity2) {
            return true;
        }

        UUID entityId1 = entity1.getUniqueId();
        UUID entityId2 = entity2.getUniqueId();
        return entityId1.equals(entityId2);
    }

    private void checkDeathUntag(@NotNull Player player) {
        ICombatManager combatManager = getCombatManager();
        MainConfiguration configuration = getConfiguration();
        if (configuration.isUntagOnSelfDeath() && combatManager.isInCombat(player)) {
            combatManager.untag(player, UntagReason.SELF_DEATH);
        }
    }

    private void checkEnemyDeathUntag(@NotNull LivingEntity enemy) {
        ICombatManager combatManager = getCombatManager();
        MainConfiguration configuration = getConfiguration();
        if (configuration.isUntagOnEnemyDeath()) {
            List<Player> playerList = combatManager.getPlayersInCombat();
            for (Player player : playerList) {
                combatManager.untag(player, enemy, UntagReason.ENEMY_DEATH);
            }
        }
    }

    private void runTagCommands(@NotNull Player player, @Nullable Entity enemy) {
        ICombatLogX plugin = getCombatLogX();
        CommandConfiguration commandConfiguration = plugin.getCommandConfiguration();
        List<String> tagCommandList = commandConfiguration.getTagCommands();

        List<Entity> enemyList = (enemy == null ? Collections.emptyList() : Collections.singletonList(enemy));
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        placeholderManager.runReplacedCommands(player, enemyList, tagCommandList);
    }

    private boolean isDisabled(@NotNull TagReason reason) {
        MainConfiguration configuration = getConfiguration();
        Set<TagReason> tagReasons = configuration.getEnabledTagReasons();
        return !tagReasons.contains(reason);
    }
}
