package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.OldUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class FinalMonitor implements Listener {
    @EventHandler(priority=EventPriority.MONITOR)
    public void pce(PlayerCombatEvent e) {
        if(e.isCancelled()) return;
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();
        
        if(ConfigOptions.OPTION_LOG_TO_FILE) {
            String msg = Combat.log(ler, led);
            if(ConfigOptions.OPTION_LOG_TO_CONSOLE) Util.print(msg);
        }

        if(ler instanceof Player) {
            Player p = (Player) ler;
            if(CombatUtil.canBeTagged(p)) {
                boolean in = Combat.isInCombat(p);
                if(!in) {
                    String pname = OldUtil.getName(p);
                    String ename = OldUtil.getName(led);
                    List<String> l1 = Util.newList("{attacker}", "{target}");
                    List<Object> l2 = Util.newList(pname, ename);
                    boolean p2 = (led instanceof Player);
                    String msg = p2 ? Util.formatMessage(ConfigLang.MESSAGE_ATTACK, l1, l2) : Util.formatMessage(ConfigLang.MESSAGE_ATTACK_MOB, l1, l2);
                    Util.sendMessage(p, msg);
                } Combat.tag(p, led);
            }
        }

        if(led instanceof Player) {
            Player p = (Player) led;
            if(CombatUtil.canBeTagged(p)) {
                boolean in = Combat.isInCombat(p);
                if(!in) { 
                    String pname = OldUtil.getName(p);
                    String ename = OldUtil.getName(ler);
                    List<String> l1 = Util.newList("{attacker}", "{target}");
                    List<Object> l2 = Util.newList(ename, pname);
                    boolean p2 = (ler instanceof Player);
                    String msg = p2 ? Util.formatMessage(ConfigLang.MESSAGE_TARGET, l1, l2) : Util.formatMessage(ConfigLang.MESSAGE_TARGET_MOB, l1, l2);
                    Util.sendMessage(p, msg);
                } Combat.tag(p, ler);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void pue(PlayerUntagEvent e) {
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        UntagCause uc = e.getCause();
        switch(uc) {
            case EXPIRE: 
                Util.sendMessage(p, ConfigLang.MESSAGE_EXPIRE);
                break;

            case KICK: 
                if(ConfigOptions.PUNISH_ON_KICK) Combat.punish(p);
                break;
                
            case QUIT: 
                if(ConfigOptions.PUNISH_ON_QUIT) Combat.punish(p);
                break;
            
            case ENEMY_DEATH:
                List<String> l1 = Util.newList("{enemy_name}");
                LivingEntity enemy = Combat.getEnemy(p);
                List<String> l2 = Util.newList(OldUtil.getName(Combat.getEnemy(p)));
                String msg = Util.formatMessage((enemy instanceof Player) ? ConfigLang.MESSAGE_ENEMY_DEATH_PLAYER : ConfigLang.MESSAGE_ENEMY_DEATH_MOB, l1, l2);
                Util.sendMessage(p, msg);
                break;
        }
        Combat.remove(p);
    }
}