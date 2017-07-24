package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.listener.event.*;
import com.SirBlobman.combatlogx.listener.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.*;

import java.util.List;

public class FinalMonitor implements Listener {
    @EventHandler(priority=EventPriority.MONITOR)
    public void pce(PlayerCombatEvent e) {
        if(e.isCancelled()) return;
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();

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
                    String msg = p2 ? Util.formatMessage(Config.MESSAGE_ATTACK, l1, l2) : Util.formatMessage(Config.MESSAGE_ATTACK_MOB, l1, l2);
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
                    String msg = p2 ? Util.formatMessage(Config.MESSAGE_TARGET, l1, l2) : Util.formatMessage(Config.MESSAGE_TARGET_MOB, l1, l2);
                    Util.sendMessage(p, msg);
                } Combat.tag(p, ler);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        UntagCause uc = e.getCause();
        switch(uc) {
            case EXPIRE: 
                Util.sendMessage(p, Config.MESSAGE_EXPIRE);
                break;

            case KICK: 
                if(Config.PUNISH_ON_KICK) punish(p);
                break;
                
            case QUIT: 
                if(Config.PUNISH_ON_QUIT) punish(p);
                break;
        }
        Combat.remove(p);
    }

    private void punish(Player p) {
        if(Config.PUNISH_KILL_PLAYER) p.setHealth(0.0D);

        if(Config.PUNISH_ON_QUIT_MESSAGE) {
            List<String> l1 = Util.newList("{player}");
            List<String> l2 = Util.newList(p.getName());
            String msg = Util.formatMessage(Config.MESSAGE_QUIT, l1, l2);
            Util.broadcast(msg);
        }

        if(Config.PUNISH_SUDO_LOGGERS) {
            List<String> list = Config.PUNISH_COMMANDS_LOGGERS;
            for(String cmd : list) p.performCommand(cmd); 
        }

        if(Config.PUNISH_CONSOLE) {
            List<String> list = Config.PUNISH_COMMANDS_CONSOLE;
            for(String cmd : list) Bukkit.dispatchCommand(Util.CONSOLE, cmd);
        }
    }
}