package com.SirBlobman.expansion.worldguard.utility.v7_0;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.utility.CombatUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;

public class ListenV7 implements Listener {    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onWorldGuardDenyPvP(DisallowedPVPEvent e) {
        if(isNoEntryModeVulnerable()) return;
        
        Player player = e.getDefender();
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(true);
        sendMessage(player, enemy);
    }
    
    public static boolean isNoEntryModeVulnerable() {
        try {
            Class<?> class_ConfigWG = Class.forName("com.SirBlobman.expansion.worldguard.config.ConfigWG");
            Method method_ConfigWG_getNoEntryMode = class_ConfigWG.getMethod("getNoEntryMode");
            
            Object noEntryMode = method_ConfigWG_getNoEntryMode.invoke(null);
            Class<?> class_NoEntryMode = getInnerClass(class_ConfigWG, "NoEntryMode");
            Field field_VULNERABLE = class_NoEntryMode.getField("VULNERABLE");
            Object vulnerable = field_VULNERABLE.get(null);
            
            return (noEntryMode.equals(vulnerable));
        } catch(Exception ex) {
            return false;
        }
    }
    
    public static void sendMessage(Player player, LivingEntity enemy) {
        try {
            Class<?> class_ListenWorldGuard = Class.forName("com.SirBlobman.expansion.worldguard.listener.ListenWorldGuard");
            Method method_sendMessage = class_ListenWorldGuard.getMethod("sendMessage", Player.class, LivingEntity.class);
            method_sendMessage.invoke(null, player, enemy);
        } catch(Exception ex) {
            return;
        }
    }
    
    private static Class<?> getInnerClass(Class<?> original, String innerClassName) {
        try {
            Class<?>[] classes = original.getClasses();
            for (Class<?> clazz : classes) {
                String name = clazz.getSimpleName();
                if (name.equals(innerClassName)) return clazz;
            }
            return null;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
