package com.github.sirblobman.combatlogx.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import org.jetbrains.annotations.Contract;

public final class ListenerDamage extends CombatListener {
    public ListenerDamage(ICombatLogX plugin) {
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
        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("link-fishing-rod")) {
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

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        return configurationManager.get("config.yml");
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
        YamlConfiguration configuration = getConfiguration();

        if (configuration.getBoolean("link-projectiles")) {
            entity = EntityHelper.linkProjectile(plugin, entity);
        }

        if (configuration.getBoolean("link-pets")) {
            entity = EntityHelper.linkPet(entity);
        }

        if (configuration.getBoolean("link-tnt")) {
            entity = EntityHelper.linkTNT(entity);
        }

        return entity;
    }

    private void checkTag(Entity entity, Entity enemy, TagReason tagReason) {
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

        plugin.printDebug("Triggering tag for player " + getName(playerEntity) + " with enemy "
                + getName(playerEnemy) + "...");
        boolean tag = combatManager.tag(playerEntity, playerEnemy, TagType.PLAYER, tagReason);
        plugin.printDebug("CombatTag Status: " + tag);
    }

    private String getName(Entity entity) {
        ICombatLogX plugin = getCombatLogX();
        if (entity == null) {
            CommandSender console = Bukkit.getConsoleSender();
            LanguageManager languageManager = plugin.getLanguageManager();
            String message = languageManager.getMessageString(console, "placeholder.unknown-enemy");
            return MessageUtility.color(message);
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }
}
