package com.SirBlobman.combatlogx.expansion.compatibility.mythicmobs.listener;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagReason;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.mythicmobs.CompatibilityMythicMobs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public final class ListenerMythicMobs implements Listener {
    private final Expansion expansion;

    public ListenerMythicMobs(CompatibilityMythicMobs expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if(enemy == null) return;
        
        String mobName = getMythicMobName(enemy);
        if(mobName == null) return;
        
        printDebug("Detected Mythic Mob '" + mobName + "' in PlayerPreTagEvent:");
        boolean preventTag = shouldPreventTag(mobName);
        
        if(preventTag) {
            printDebug("Mob is in tag prevention list, cancelling PlayerPreTagEvent.");
            e.setCancelled(true);
        }
        
        printDebug("Mob is not in tag prevention list, ignoring...");
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();
        
        if(damager instanceof Player) {
            Player player = (Player) damager;
            checkForceTag(player, damaged, TagReason.ATTACKER);
        }
        
        if(damaged instanceof Player) {
            Player player = (Player) damaged;
            checkForceTag(player, damager, TagReason.ATTACKED);
        }
    }

    private BukkitAPIHelper getMythicMobsAPI() {
        MythicMobs mythicMobs = JavaPlugin.getPlugin(MythicMobs.class);
        return mythicMobs.getAPIHelper();
    }

    private boolean isMythicMob(Entity entity) {
        BukkitAPIHelper apiHelper = getMythicMobsAPI();
        return apiHelper.isMythicMob(entity);
    }

    private String getMythicMobName(Entity entity) {
        if(isMythicMob(entity)) {
            BukkitAPIHelper apiHelper = getMythicMobsAPI();
            ActiveMob activeMob = apiHelper.getMythicMobInstance(entity);
            return (activeMob == null ? null : activeMob.getMobType());
        }

        return null;
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        return configurationManager.get("mythicmobs-compatibility.yml");
    }

    private void checkForceTag(Player player, Entity enemy, TagReason reason) {
        String mobName = getMythicMobName(enemy);
        if(mobName == null || !shouldForceTag(mobName)) return;

        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        if(enemy instanceof LivingEntity) {
            LivingEntity livingEnemy = (LivingEntity) enemy;
            combatManager.tag(player, livingEnemy, TagType.MOB, reason);
            return;
        }

        combatManager.tag(player, null, TagType.UNKNOWN, reason);
    }
    
    private boolean shouldPreventTag(String mobName) {
        YamlConfiguration configuration = getConfiguration();
        List<String> noTagMobNameList = configuration.getStringList("no-tag-mob-types");
        return noTagMobNameList.contains(mobName);
    }
    
    private boolean shouldForceTag(String mobName) {
        printDebug("Checking mob name '" + mobName + "' for force tag...");
        YamlConfiguration configuration = getConfiguration();
        List<String> forceTagMobNameList = configuration.getStringList("force-tag-mob-types");
        
        boolean forceTag = forceTagMobNameList.contains(mobName);
        printDebug("Force Tag: " + forceTag);
        return forceTag;
    }
    
    private void printDebug(String... messageArray) {
        for(String message : messageArray) {
            String realMessage = ("[MythicMobs Compatibility] " + message);
            this.expansion.getPlugin().printDebug(realMessage);
        }
    }
}
