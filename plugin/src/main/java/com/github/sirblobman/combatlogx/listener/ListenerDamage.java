package com.github.sirblobman.combatlogx.listener;

import com.github.puregero.multilib.MultiLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

public final class ListenerDamage extends CombatListener {
    public ListenerDamage(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        printDebug("Detected EntityDamageByEntityEvent.");

        Entity damaged = e.getEntity();
        Entity damager = getDamager(e);
        if (damager == null) {
            printDebug("Damager is null, ignoring event.");
            return;
        }

        printDebug("Damager Name + Type: " + getName(damager) + " " + damager.getType().name());
        printDebug("Damaged Name + Type: " + getName(damaged) + " " + damaged.getType().name());

        checkTag(damager, damaged, TagReason.ATTACKER);
        checkTag(damaged, damager, TagReason.ATTACKED);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        MainConfiguration configuration = getConfiguration();
        if (!configuration.isLinkFishingRod()) {
            return;
        }

        State state = e.getState();
        if (state != State.CAUGHT_ENTITY) {
            return;
        }

        Entity caughtEntity = e.getCaught();
        if (caughtEntity == null) {
            return;
        }

        Player player = e.getPlayer();
        checkTag(player, caughtEntity, TagReason.ATTACKER);
    }

    private MainConfiguration getConfiguration() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getConfiguration();
    }

    private Entity getDamager(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        return getDamager(entity);
    }

    @Contract("null -> null")
    private Entity getDamager(Entity entity) {
        if (entity == null) {
            return null;
        }

        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = getConfiguration();

        if (configuration.isLinkProjectiles()) {
            entity = EntityHelper.linkProjectile(plugin, entity);
        }

        if (configuration.isLinkPets()) {
            entity = EntityHelper.linkPet(entity);
        }

        if (configuration.isLinkTnt()) {
            entity = EntityHelper.linkTNT(entity);
        }

        if (configuration.isLinkEndCrystals()) {
            ICombatLogX combatLogX = getCombatLogX();
            ICrystalManager crystalManager = combatLogX.getCrystalManager();

            Player player = crystalManager.getPlacer(entity);
            if (player != null) {
                entity = player;
            }
        }

        return entity;
    }

    private void checkTag(@NotNull Entity entity, @NotNull Entity enemy, @NotNull TagReason tagReason) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = getCombatManager();
        plugin.printDebug("Checking if the entity '" + getName(entity) + "' should be tagged " +
                "for reason '" + tagReason + "' by enemy '" + getName(enemy) + "'.");

        if (!(entity instanceof Player)) {
            plugin.printDebug("Entity was not a player.");
            return;
        }

        if (!(enemy instanceof Player)) {
            plugin.printDebug("Enemy was not a player.");
            return;
        }

        Player playerEntity = (Player) entity;
        Player playerEnemy = (Player) enemy;

        if (MultiLib.isExternalPlayer(playerEntity) && tagReason == TagReason.ATTACKER) {
            MultiLib.notify("com.github.sirblobman.combatlogx.listener.ListenerDamage.checkTagEntity", playerEntity.getName() + " " + playerEnemy.getName());
        }
        if (MultiLib.isExternalPlayer(playerEnemy) && tagReason == TagReason.ATTACKED) {
            MultiLib.notify("com.github.sirblobman.combatlogx.listener.ListenerDamage.checkTagEnemy", playerEntity.getName() + " " + playerEnemy.getName());
        }

        plugin.printDebug("Triggering tag for player " + getName(playerEntity) + " with enemy "
                + getName(playerEnemy) + "...");
        boolean tag = combatManager.tag(playerEntity, playerEnemy, TagType.PLAYER, tagReason);
        plugin.printDebug("CombatTag Status: " + tag);
    }

    private String getName(@NotNull Entity entity) {
        ICombatLogX plugin = getCombatLogX();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }
}
