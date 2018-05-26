package com.SirBlobman.not;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.not.config.ConfigNot;

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
        
        ConfigNot.load();
        Util.regEvents(this, new FinalMonitor());
    }

    public String getUnlocalizedName() {return getName();}
    public String getName() {return "NotCombatLogX";}
    public String getVersion() {return "6";}
    
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
                    if(ConfigNot.TRIGGER_ALL_DAMAGE) {
                        String msg = Util.color(ConfigNot.MESSAGE_UNKNOWN);
                        Util.sendMessage(p, msg);
                        call(p);
                    } else {
                        boolean send = false;
                        String msg = "";
                        if(dc == DamageCause.DROWNING && ConfigNot.TRIGGER_DROWNING) {
                            send = true;
                            msg = ConfigNot.MESSAGE_DROWNING;
                        } else if(dc == DamageCause.BLOCK_EXPLOSION && ConfigNot.TRIGGER_EXPLOSION) {
                            send = true;
                            msg = ConfigNot.MESSAGE_EXPLOSION;
                        } else if(dc == DamageCause.LAVA && ConfigNot.TRIGGER_LAVA) {
                            send = true;
                            msg = ConfigNot.MESSAGE_LAVA;
                        } else if(dc == DamageCause.FALL && ConfigNot.TRIGGER_FALL) {
                            send = true;
                            msg = ConfigNot.MESSAGE_FALL;
                        } else if(dc == DamageCause.PROJECTILE && ConfigNot.TRIGGER_PROJECTILE) {
                            EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
                            Entity enp = edbee.getDamager();
                            if(enp instanceof Projectile) {
                                Projectile pj = (Projectile) enp;
                                ProjectileSource ps = pj.getShooter();
                                if(!(ps instanceof Entity)) {
                                    send = true;
                                    msg = ConfigNot.MESSAGE_PROJECTILE;
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
    
    public static void call(Player p) {
        SpecialCombatEvent sce = new SpecialCombatEvent(p);
        Util.call(sce);
    }
}