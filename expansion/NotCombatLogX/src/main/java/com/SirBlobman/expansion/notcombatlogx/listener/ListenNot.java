package com.SirBlobman.expansion.notcombatlogx.listener;

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

import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notcombatlogx.config.ConfigNot;

public class ListenNot implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        if(e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            Entity damager = ee.getDamager();
            if(damager instanceof LivingEntity) return;
            
            if(damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if(projectile.getShooter() instanceof LivingEntity) return;
            }
        }
        
        Player player = (Player) entity;
        DamageCause cause = e.getCause();
        if(!ConfigNot.canDamageTypeTagPlayer(cause)) return;
        
        boolean wasInCombat = CombatUtil.isInCombat(player);
        boolean wasTagged = CombatUtil.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        if(wasTagged && !wasInCombat) {
            String message = ConfigNot.getTagMessage(cause);
            Util.sendMessage(player, message);
        }
    }
}