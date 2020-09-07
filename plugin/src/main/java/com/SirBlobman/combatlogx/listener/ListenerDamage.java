package com.SirBlobman.combatlogx.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.nms.EntityHandler;
import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.CombatPlugin;
import com.SirBlobman.combatlogx.api.object.TagReason;
import com.SirBlobman.combatlogx.api.object.TagType;
import com.SirBlobman.combatlogx.api.utility.EntityHelper;
import com.SirBlobman.combatlogx.manager.CombatManager;

public class ListenerDamage extends CombatListener {
    public ListenerDamage(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = getDamager(e);
        checkTag(damaged, damager, TagReason.ATTACKED);
        checkTag(damager, damaged, TagReason.ATTACKER);
    }

    private Entity getDamager(EntityDamageByEntityEvent e) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        Entity damager = e.getDamager();
        if(configuration.getBoolean("link-projectiles")) damager = EntityHelper.linkProjectile(damager);
        if(configuration.getBoolean("link-pets")) damager = EntityHelper.linkPet(damager);
        return damager;
    }

    private void checkTag(Entity entity, Entity enemy, TagReason tagReason) {
        CombatPlugin plugin = getPlugin();
        CombatManager combatManager = plugin.getCombatManager();
        plugin.printDebug("Checking if the entity '" + getName(entity) + "' should be tagged for reason '" + tagReason + "' by enemy '" + getName(enemy) + "'.");

        if(!(entity instanceof Player)) {
            plugin.printDebug("Entity was not a player.");
            return;
        }

        if(!(enemy instanceof Player)) {
            plugin.printDebug("Enemy was not a playe.");
            return;
        }

        Player playerEntity = (Player) entity;
        Player playerEnemy = (Player) enemy;

        plugin.printDebug("Triggering tag for player " + getName(playerEntity) + " with enemy " + getName(playerEnemy) + "...");
        boolean tag = combatManager.tag(playerEnemy, playerEntity, TagType.PLAYER, tagReason);
        plugin.printDebug("Tag Status: " + tag);
    }

    private String getName(Entity entity) {
        CombatPlugin plugin = getPlugin();
        if(entity == null) {
            CommandSender console = Bukkit.getConsoleSender();
            LanguageManager languageManager = plugin.getLanguageManager();
            return languageManager.getMessage(console, "placeholder.unknown-enemy");
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }
}