package com.SirBlobman.expansion.notcombatlogx;

import org.bukkit.World;
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

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notcombatlogx.config.ConfigNot;

import java.io.File;

public class NotCombatLogX implements CLXExpansion, Listener {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "NotCombatLogX";
    }

    public String getVersion() {
        return "14.1";
    }

    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigNot.load();
        PluginUtil.regEvents(this);
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {
        FOLDER = getDataFolder();
        ConfigNot.load();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        World world = entity.getWorld();
        String worldName = world.getName();
        
        if(ConfigOptions.OPTION_DISABLED_WORLDS.contains(worldName)) return;
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
        DamageCause damage = e.getCause();
        if(!ConfigNot.canDamageTypeTagPlayer(damage)) return;
        
        if(!CombatUtil.isInCombat(player)) {
            String msg = ConfigNot.getTagMessage(damage);
            Util.sendMessage(player, msg);
        }
        
        CombatUtil.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
    }
}