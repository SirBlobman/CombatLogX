package com.SirBlobman.not;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.*;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

public class NotCombatLogX implements CLXExpansion, Listener {
    @Override
    public void enable() {
        NConfig.load();
        Util.regEvents(this, new FinalMonitor());
    }

    @Override
    public String getName() {return "NotCombatLogX";}
    
    @Override
    public String getVersion() {return "4.0.0 Alpha";}
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void ede(EntityDamageEvent e) {
        if(e.isCancelled()) return;
        Entity en = e.getEntity();
        if(en instanceof Player) {
            Player p = (Player) en;
            if(CombatUtil.canBeTagged(p)) {
                DamageCause dc = e.getCause();
                if(dc == DamageCause.PROJECTILE && NConfig.TRIGGER_PROJECTILE) {
                    Entity enp = ((EntityDamageByEntityEvent) e).getDamager();
                    if(enp instanceof Projectile) {
                        Projectile pj = (Projectile) enp;
                        ProjectileSource ps = pj.getShooter();
                        if(!(ps instanceof Entity)) {
                            String msg = NConfig.MESSAGE_PROJECTILE;
                            if(!Combat.isInCombat(p)) Util.sendMessage(p, msg);
                            call(p);
                        }
                    }
                } else if(dc == DamageCause.DROWNING && NConfig.TRIGGER_DROWNING) {
                    String msg = NConfig.MESSAGE_DROWNING;
                    if(!Combat.isInCombat(p)) Util.sendMessage(p, msg);
                    call(p);
                } else if(dc == DamageCause.BLOCK_EXPLOSION && NConfig.TRIGGER_EXPLOSION) {
                    String msg = NConfig.MESSAGE_EXPLOSION;
                    if(!Combat.isInCombat(p)) Util.sendMessage(p, msg);
                    call(p);
                } else if(dc == DamageCause.LAVA && NConfig.TRIGGER_LAVA) {
                    String msg = NConfig.MESSAGE_LAVA;
                    if(!Combat.isInCombat(p)) Util.sendMessage(p, msg);
                    call(p);
                } else if(dc == DamageCause.FALL && NConfig.TRIGGER_FALL) {
                    String msg = NConfig.MESSAGE_FALL;
                    if(!Combat.isInCombat(p)) Util.sendMessage(p, msg);
                    call(p);
                } else if(NConfig.TRIGGER_ALL_DAMAGE) {
                    String msg = NConfig.MESSAGE_UNKNOWN;
                    if(!Combat.isInCombat(p)) Util.sendMessage(p, msg);
                    call(p);
                }
            }
        }
    }
    
    private void call(Player p) {
        SpecialCombatEvent sce = new SpecialCombatEvent(p);
        Util.call(sce);
    }
}