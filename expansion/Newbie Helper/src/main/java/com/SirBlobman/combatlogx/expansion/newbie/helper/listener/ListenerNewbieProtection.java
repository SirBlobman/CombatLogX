package com.SirBlobman.combatlogx.expansion.newbie.helper.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ListenerNewbieProtection implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerNewbieProtection(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEntityCombat(EntityDamageByEntityEvent e) {
        Entity ded = e.getEntity();
        if(!(ded instanceof Player)) return;

        Entity der = linkPet(linkProjectile(e.getDamager()));
        if(!(der instanceof Player)) return;

        Player damaged = (Player) ded;
        Player damager = (Player) der;
        ILanguageManager languageManager = this.plugin.getLanguageManager();

        if(isProtected(damaged)) {
            e.setCancelled(true);
            String message = languageManager.getMessageColoredWithPrefix("newbie-helper.no-pvp.protected");
            languageManager.sendMessage(damager, message);
            return;
        }

        if(isProtected(damager)) {
            String message = languageManager.getMessageColoredWithPrefix("newbie-helper.protection-disabled.attacker");
            languageManager.sendMessage(damager, message);
            removeProtection(damager);
        }
    }

    private boolean isProtected(Player player) {
        if(player == null || player.hasMetadata("NPC")) return false;
        long firstJoinMillis = player.getFirstPlayed();
        if(firstJoinMillis == 0) return false;

        long systemMillis = System.currentTimeMillis();
        long subtract = (systemMillis - firstJoinMillis);

        FileConfiguration config = this.expansion.getConfig("newbie-helper.yml");
        long protectionTimeMillis = config.getLong("newbie-protection.protection-timer") * 1000L;
        if(subtract > protectionTimeMillis) {
            removeProtection(player);
            return false;
        }

        YamlConfiguration dataFile = this.plugin.getDataFile(player);
        if(!dataFile.isSet("newbie-helper.protected")) {
            dataFile.set("newbie-helper.protected", true);
            this.plugin.saveDataFile(player);
        }
        
        return dataFile.getBoolean("newbie-helper.protected");
    }

    private void removeProtection(Player player) {
        if(player == null) return;
        ILanguageManager languageManager = this.plugin.getLanguageManager();

        YamlConfiguration dataFile = this.plugin.getDataFile(player);
        if(!dataFile.getBoolean("newbie-helper.protected", true)) return;

        dataFile.set("newbie-helper.protected", false);
        this.plugin.saveDataFile(player);

        String message = languageManager.getMessageColoredWithPrefix("newbie-helper.protection-disabled.expired");
        languageManager.sendMessage(player, message);
    }

    private Entity linkProjectile(Entity entity) {
        FileConfiguration config = this.expansion.getConfig("newbie-helper.yml");
        if(!config.getBoolean("pvp-checks.link-projectiles")) return entity;
        if(!(entity instanceof Projectile)) return entity;

        Projectile projectile = (Projectile) entity;
        ProjectileSource shooter = projectile.getShooter();
        return (shooter instanceof Entity ? (Entity) shooter : entity);
    }

    private Entity linkPet(Entity entity) {
        FileConfiguration config = this.expansion.getConfig("newbie-helper.yml");
        if(!config.getBoolean("pvp-checks.link-pets")) return entity;
        if(!(entity instanceof Tameable)) return entity;

        Tameable pet = (Tameable) entity;
        AnimalTamer owner = pet.getOwner();
        return (owner instanceof Entity ? (Entity) owner : entity);
    }
}