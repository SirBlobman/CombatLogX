package com.SirBlobman.not;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.not.config.NConfig;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;

public class NotCombatLogX implements CLXExpansion, Listener {
    public static File FOLDER;
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        
        NConfig.load();
        Util.regEvents(this, new FinalMonitor());
    }

    @Override
    public String getName() {return "NotCombatLogX";}
    
    @Override
    public String getVersion() {return "5.0.1";}
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void ede(EntityDamageEvent e) {
        if(e.isCancelled()) return;
        Entity en = e.getEntity();
        if(en instanceof Player) {
            Player p = (Player) en;
            if(CombatUtil.canBeTagged(p)) {
                DamageCause dc = e.getCause();
                String sdc = dc.name();
                if(!sdc.contains("ENTITY")) {
                    if(NConfig.TRIGGER_ALL_DAMAGE) {
                        String msg = Util.color(NConfig.MESSAGE_UNKNOWN);
                        Util.sendMessage(p, msg);
                        call(p);
                    } else {
                        boolean send = false;
                        String msg = "";
                        if(dc == DamageCause.DROWNING && NConfig.TRIGGER_DROWNING) {
                            send = true;
                            msg = NConfig.MESSAGE_DROWNING;
                        } else if(dc == DamageCause.BLOCK_EXPLOSION && NConfig.TRIGGER_EXPLOSION) {
                            send = true;
                            msg = NConfig.MESSAGE_EXPLOSION;
                        } else if(dc == DamageCause.LAVA && NConfig.TRIGGER_LAVA) {
                            send = true;
                            msg = NConfig.MESSAGE_LAVA;
                        } else if(dc == DamageCause.FALL && NConfig.TRIGGER_FALL) {
                            send = true;
                            msg = NConfig.MESSAGE_FALL;
                        } else if(dc == DamageCause.PROJECTILE && NConfig.TRIGGER_PROJECTILE) {
                            EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
                            Entity enp = edbee.getDamager();
                            if(enp instanceof Projectile) {
                                Projectile pj = (Projectile) enp;
                                ProjectileSource ps = pj.getShooter();
                                if(!(ps instanceof Entity)) {
                                    send = true;
                                    msg = NConfig.MESSAGE_PROJECTILE;
                                } else send = false;
                            } else send = false;
                        }
                        
                        if(send) {
                            String color = Util.color(msg);
                            if(!Combat.isInCombat(p)) Util.sendMessage(p, color);
                            call(p);
                        }
                    }
                }
            }
        }
    }
    
    private void call(Player p) {
        SpecialCombatEvent sce = new SpecialCombatEvent(p);
        Util.call(sce);
    }
}