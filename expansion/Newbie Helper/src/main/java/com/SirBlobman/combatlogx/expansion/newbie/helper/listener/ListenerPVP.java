package com.SirBlobman.combatlogx.expansion.newbie.helper.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ListenerPVP implements Listener {
    private NewbieHelper expansion;
    private ICombatLogX plugin;
    public ListenerPVP(NewbieHelper expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    public void disablePVP(Player player) {
        if(player == null) return;

        YamlConfiguration playerData = this.plugin.getDataFile(player);
        playerData.set("pvp", false);
        this.plugin.saveDataFile(player, playerData);
    }

    public void enablePVP(Player player) {
        if(player == null) return;

        YamlConfiguration playerData = this.plugin.getDataFile(player);
        playerData.set("pvp", true);
        this.plugin.saveDataFile(player, playerData);
    }

    public boolean isPVPEnabled(Player player) {
        if(player == null) return false;

        YamlConfiguration playerData = this.plugin.getDataFile(player);
        return playerData.getBoolean("pvp", true);
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

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEntityCombat(EntityDamageByEntityEvent e) {
        Entity ded = e.getEntity();
        if (!(ded instanceof Player)) return;

        Entity der = linkPet(linkProjectile(e.getDamager()));
        if (!(der instanceof Player)) return;

        Player damaged = (Player) ded;
        Player damager = (Player) der;

        if(!isPVPEnabled(damaged)) {
            e.setCancelled(true);
            String message = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.no-pvp.other");
            damager.sendMessage(message);
            return;
        }

        if(!isPVPEnabled(damager)) {
            e.setCancelled(true);
            String message = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.no-pvp.self");
            damager.sendMessage(message);
            // return;
        }
    }
}