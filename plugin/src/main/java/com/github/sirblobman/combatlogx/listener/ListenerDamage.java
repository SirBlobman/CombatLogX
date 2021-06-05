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
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

public class ListenerDamage extends CombatListener {
    public ListenerDamage(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = getDamager(e);
        if(damager == null) return;

        checkTag(damaged, damager, TagReason.ATTACKED);
        checkTag(damager, damaged, TagReason.ATTACKER);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onFish(PlayerFishEvent e) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("link-fishing-rod")) return;

        State state = e.getState();
        if(state != State.CAUGHT_ENTITY) return;

        Entity caughtEntity = e.getCaught();
        if(caughtEntity == null) return;

        Player player = e.getPlayer();
        checkTag(player, caughtEntity, TagReason.ATTACKER);
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private Entity getDamager(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        return getDamager(entity);
    }

    private Entity getDamager(Entity entity) {
        if(entity == null) return null;

        ICombatLogX plugin = getPlugin();
        YamlConfiguration configuration = getConfiguration();

        if(configuration.getBoolean("link-projectiles")) {
            entity = EntityHelper.linkProjectile(plugin, entity);
        }

        if(configuration.getBoolean("link-pets")) {
            entity = EntityHelper.linkPet(entity);
        }

        return entity;
    }

    private void checkTag(Entity entity, Entity enemy, TagReason tagReason) {
        ICombatLogX plugin = getPlugin();
        ICombatManager combatManager = getCombatManager();
        plugin.printDebug("Checking if the entity '" + getName(entity) + "' should be tagged for reason '" + tagReason + "' by enemy '" + getName(enemy) + "'.");

        if(!(entity instanceof Player)) {
            plugin.printDebug("Entity was not a player.");
            return;
        }

        if(!(enemy instanceof Player)) {
            plugin.printDebug("Enemy was not a player.");
            return;
        }

        Player playerEntity = (Player) entity;
        Player playerEnemy = (Player) enemy;

        plugin.printDebug("Triggering tag for player " + getName(playerEntity) + " with enemy " + getName(playerEnemy) + "...");
        boolean tag = combatManager.tag(playerEnemy, playerEntity, TagType.PLAYER, tagReason);
        plugin.printDebug("Tag Status: " + tag);
    }

    private String getName(Entity entity) {
        ICombatLogX plugin = getPlugin();
        if(entity == null) {
            CommandSender console = Bukkit.getConsoleSender();
            LanguageManager languageManager = plugin.getLanguageManager();
            return languageManager.getMessage(console, "placeholder.unknown-enemy", null, true);
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }
}
