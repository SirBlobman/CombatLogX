package com.SirBlobman.combatlogx.expansion.damage.tagger.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.damage.tagger.DamageTagger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ListenerDamage implements Listener {
    private DamageTagger expansion;
    private ICombatLogX plugin;
    public ListenerDamage(DamageTagger expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    private boolean doesNotTagPlayer(EntityDamageEvent.DamageCause cause) {
        if(cause == null) return true;

        FileConfiguration config = this.expansion.getConfig("damage-tagger.yml");
        boolean allDamage = config.getBoolean("all-damage");
        if(allDamage) return false;

        String causeName = cause.name().toLowerCase().replace("_", "-");
        String configPath = "damage-type." + causeName;
        return !config.getBoolean(configPath, false);
    }

    private String getTagMessage(EntityDamageEvent.DamageCause cause) {
        if(doesNotTagPlayer(cause)) return null;

        FileConfiguration config = this.expansion.getConfig("damage-tagger.yml");
        boolean allDamage = config.getBoolean("all-damage");
        if(allDamage) return this.plugin.getLanguageMessageColoredWithPrefix("damage-tagger.unknown-damage");

        String causeName = cause.name().toLowerCase().replace("_", "-");
        String messageKey = "damage-tagger.damage-type." + causeName;
        return this.plugin.getLanguageMessageColoredWithPrefix(messageKey);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        if(checkDamageByEntity(e)) return;

        Player player = (Player) entity;
        EntityDamageEvent.DamageCause cause = e.getCause();
        if(doesNotTagPlayer(cause)) return;

        ICombatManager combatManager = this.plugin.getCombatManager();
        boolean wasInCombat = combatManager.isInCombat(player);

        boolean tagged = combatManager.tag(player, null, PlayerPreTagEvent.TagType.UNKNOWN, PlayerPreTagEvent.TagReason.UNKNOWN);
        if(!wasInCombat && tagged) {
            String message = getTagMessage(cause);
            this.plugin.sendMessage(player, message);
        }
    }

    private boolean checkDamageByEntity(EntityDamageEvent event) {
        if(!(event instanceof EntityDamageByEntityEvent)) return false;

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Entity damager = e.getDamager();
        if(damager instanceof LivingEntity) return true;

        if(damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            ProjectileSource shooter = projectile.getShooter();
            return (shooter instanceof LivingEntity);
        }

        return false;
    }
}