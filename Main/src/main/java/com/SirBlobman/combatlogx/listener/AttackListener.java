package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AttackListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        if ((damager instanceof Projectile) && ConfigOptions.OPTION_LINK_PROJECTILES) {
            Projectile p = (Projectile) damager;
            if (!p.getType().equals(EntityType.ENDER_PEARL)) {
                ProjectileSource ps = p.getShooter();
                if (ps instanceof Entity) {
                    damager = (Entity) ps;
                }
            }
        }

        if ((damager instanceof Tameable) && ConfigOptions.OPTION_LINK_PETS) {
            Tameable t = (Tameable) damager;
            AnimalTamer at = t.getOwner();
            if (at instanceof Entity) {
                damager = (Entity) at;
            }
        }

        if (damaged instanceof LivingEntity && damager instanceof LivingEntity) {
            if (damaged instanceof Player) {
                Player p = (Player) damaged;
                LivingEntity enemy = (LivingEntity) damager;
                TagType type = (damager instanceof Player) ? TagType.PLAYER : TagType.MOB;
                TagReason reason = TagReason.ATTACKED;
                CombatUtil.tag(p, enemy, type, reason);
            }

            if (damager instanceof Player) {
                Player p = (Player) damager;
                LivingEntity enemy = (LivingEntity) damaged;
                TagType type = damaged instanceof Player ? TagType.PLAYER : TagType.MOB;
                TagReason reason = TagReason.ATTACKER;
                CombatUtil.tag(p, enemy, type, reason);
            }
        }
    }
}