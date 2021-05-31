package com.github.sirblobman.combatlogx.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class ListenerFish extends CombatListener {
    public ListenerFish(ICombatLogX plugin) {
        super(plugin);
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private void checkTag(Entity entity, Entity enemy) {
        ICombatLogX plugin = getPlugin();
        ICombatManager combatManager = getCombatManager();
        plugin.printDebug("Checking if the entity '" + getName(entity) + "' should be tagged for reason '" + TagReason.ATTACKER + "' by enemy '" + getName(enemy) + "'.");

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
        boolean tag = combatManager.tag(playerEnemy, playerEntity, TagType.PLAYER, TagReason.ATTACKER);
        plugin.printDebug("Tag Status: " + tag);
    }

    private String getName(Entity entity) {
        ICombatLogX plugin = getPlugin();
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
