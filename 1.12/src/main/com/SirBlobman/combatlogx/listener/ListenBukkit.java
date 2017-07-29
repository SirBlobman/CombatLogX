package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.listener.event.*;
import com.SirBlobman.combatlogx.listener.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.*;

import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public class ListenBukkit implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void eve(EntityDamageByEntityEvent e) {
        if(e.isCancelled()) return;
        Entity ded = e.getEntity();
        Entity der = e.getDamager();
        
        List<String> worlds = Config.OPTION_DISABLED_WORLDS;
        World w = ded.getWorld();
        String wn = w.getName();
        if(worlds.contains(wn)) return;
        
        if(der instanceof Projectile) {
            Projectile p = (Projectile) der;
            ProjectileSource ps = p.getShooter();
            if(ps instanceof Entity) {
                Entity en = (Entity) ps;
                der = en;
            }
        }
        
        if(der instanceof Tameable) {
            Tameable t = (Tameable) der;
            AnimalTamer at = t.getOwner();
            if(at != null && at instanceof Entity) der = (Entity) at;
        }
        
        if(ded instanceof LivingEntity && der instanceof LivingEntity) {
            LivingEntity led = (LivingEntity) ded;
            LivingEntity ler = (LivingEntity) der;
            if(CombatUtil.canAttack(ler, led)) {
                boolean p1 = (led instanceof Player);
                boolean p2 = (ler instanceof Player);
                if(p1) {
                    Player p = (Player) led;
                    PlayerCombatEvent pce = new PlayerCombatEvent(p, ler, false);
                    Util.call(pce);
                }
                
                if(p2) {
                    Player p = (Player) ler;
                    PlayerCombatEvent pce = new PlayerCombatEvent(p, led, true);
                    Util.call(pce);
                }
                
                if(!p1 && !p2) {
                    CombatEvent ce = new CombatEvent(ler, led, true);
                    Util.call(ce);
                }
            }
        }
    }
    
    @EventHandler
    public void death(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if(Combat.isInCombat(p)) {
            UntagCause uc = UntagCause.EXPIRE;
            PlayerUntagEvent pue = new PlayerUntagEvent(p, uc);
            Util.call(pue);
        }
    }
    
    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if(Combat.isInCombat(p)) {
            UntagCause uc = UntagCause.EXPIRE;
            PlayerUntagEvent pue = new PlayerUntagEvent(p, uc);
            Util.call(pue);
        }
    }
    
    @EventHandler
    public void death(EntityDeathEvent e) {
        if(Config.OPTION_REMOVE_COMBAT_ON_ENEMY_DEATH) {
            LivingEntity le = e.getEntity();
            List<LivingEntity> list = Combat.enemyList();
            if(list.contains(le)) {
                Player p = Combat.getByEnemy(le);
                if(p != null) {
                    UntagCause uc = UntagCause.ENEMY_DEATH;
                    PlayerUntagEvent pue = new PlayerUntagEvent(p, uc);
                    Util.call(pue);
                }
            }
        }
    }
    
    @EventHandler
    public void kick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if(Combat.isInCombat(p)) {
            UntagCause uc = UntagCause.KICK;
            PlayerUntagEvent pue = new PlayerUntagEvent(p, uc);
            Util.call(pue);
        }
    }
    
    @EventHandler
    public void quit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(Combat.isInCombat(p)) {
            UntagCause uc = UntagCause.QUIT;
            PlayerUntagEvent pue = new PlayerUntagEvent(p, uc);
            Util.call(pue);
        }
    }
}