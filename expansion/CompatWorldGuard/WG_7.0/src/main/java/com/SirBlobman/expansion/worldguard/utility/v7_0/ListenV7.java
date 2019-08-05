package com.SirBlobman.expansion.worldguard.utility.v7_0;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;

import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;

public class ListenV7 implements Listener {
	private final NoEntryExpansion expansion;
	public ListenV7(NoEntryExpansion expansion) {
		this.expansion = expansion;
	}
	
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onWorldGuardDenyPvP(DisallowedPVPEvent e) {
        if(!isNoEntryModeVulnerable()) return;
        
        Player player = e.getDefender();
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
    
    public static boolean isNoEntryModeVulnerable() {
        try {
            Class<?> class_ConfigWG = Class.forName("com.SirBlobman.expansion.worldguard.config.ConfigWG");
            Method method_ConfigWG_getNoEntryMode = class_ConfigWG.getMethod("getNoEntryMode");
            
            Object noEntryMode = method_ConfigWG_getNoEntryMode.invoke(null);
            Class<?> class_NoEntryMode = getInnerClass(NoEntryExpansion.class, "NoEntryMode");
            Field field_VULNERABLE = class_NoEntryMode.getField("VULNERABLE");
            Object vulnerable = field_VULNERABLE.get(null);
            
            return (noEntryMode.equals(vulnerable));
        } catch(ReflectiveOperationException ex) {
            return false;
        }
    }
    
    private static Class<?> getInnerClass(Class<?> original, String innerClassName) {
    	Class<?>[] classes = original.getClasses();
    	for(Class<?> clazz : classes) {
    		String className = clazz.getSimpleName();
    		if(className.equals(innerClassName)) return clazz;
    	}
    	
    	return null;
    }
}