package com.SirBlobman.combatlogx.expansion.damage.tagger.listener;

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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagReason;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.expansion.damage.tagger.DamageTagger;

public class ListenerDamage implements Listener {
    private final DamageTagger expansion;
    private final ICombatLogX plugin;
    public ListenerDamage(DamageTagger expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        if(checkDamageByEntity(e)) return;

        Player player = (Player) entity;
        DamageCause damageCause = e.getCause();
        if(doesNotTagPlayer(damageCause)) return;

        ICombatManager combatManager = this.plugin.getCombatManager();
        ILanguageManager languageManager = this.plugin.getLanguageManager();
        boolean wasInCombat = combatManager.isInCombat(player);

        boolean tagged = combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        if(!wasInCombat && tagged) {
            String message = getTagMessage(damageCause);
            languageManager.sendMessage(player, message);
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

    private boolean doesNotTagPlayer(DamageCause damageCause) {
        if(damageCause == null) return true;

        FileConfiguration config = this.expansion.getConfig("damage-tagger.yml");
        boolean allDamage = config.getBoolean("all-damage");
        if(allDamage) return false;

        String causeName = damageCause.name().toLowerCase().replace("_", "-");
        String configPath = "damage-type." + causeName;
        return !config.getBoolean(configPath, false);
    }

    private String getTagMessage(DamageCause damageCause) {
        if(doesNotTagPlayer(damageCause)) return null;
        ILanguageManager languageManager = this.plugin.getLanguageManager();

        FileConfiguration config = this.expansion.getConfig("damage-tagger.yml");
        boolean allDamage = config.getBoolean("all-damage");
        if(allDamage) return languageManager.getMessageColoredWithPrefix("damage-tagger.unknown-damage");

        String causeName = damageCause.name().toLowerCase().replace("_", "-");
        String messageKey = "damage-tagger.damage-type." + causeName;
        return languageManager.getMessageColoredWithPrefix(messageKey);
    }
}