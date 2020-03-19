package com.SirBlobman.combatlogx.expansion.mob.tagger.listener;

import java.util.List;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.mob.tagger.MobTagger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ListenerMobCombat implements Listener {
    private final MobTagger expansion;
    public ListenerMobCombat(MobTagger expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();
        
        if(!(damaged instanceof LivingEntity)) return;
        LivingEntity attacked = (LivingEntity) damaged;
        
        if(!(damager instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity) damager;
        
        if(attacked instanceof Player && !(attacker instanceof Player)) {
            Player player = (Player) attacked;
            PlayerPreTagEvent.TagReason tagReason = PlayerPreTagEvent.TagReason.ATTACKED;
            
            ICombatManager combatManager = this.expansion.getPlugin().getCombatManager();
            combatManager.tag(player, attacker, TagType.MOB, tagReason);
        }
        
        if(attacker instanceof Player && !(attacked instanceof Player)) {
            Player player = (Player) attacker;
            PlayerPreTagEvent.TagReason tagReason = PlayerPreTagEvent.TagReason.ATTACKER;
    
            ICombatManager combatManager = this.expansion.getPlugin().getCombatManager();
            combatManager.tag(player, attacked, TagType.MOB, tagReason);
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if(enemy == null) return;
    
        if(checkMobTypeDisabled(enemy)) {
            e.setCancelled(true);
            return;
        }
        
        if(checkMobSpawnReasonDisabled(enemy)) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSpawn(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        SpawnReason spawnReason = e.getSpawnReason();
    
        FixedMetadataValue fixedValue = new FixedMetadataValue(this.expansion.getPlugin().getPlugin(), spawnReason);
        entity.setMetadata("combatlogx_spawn_reason", fixedValue);
    }
    
    private boolean checkMobTypeDisabled(LivingEntity enemy) {
        if(enemy == null) return false;
        
        FileConfiguration config = this.expansion.getConfig("mob-tagger.yml");
        boolean mobsDisabled = !config.getBoolean("tag-players");
        if(mobsDisabled && !(enemy instanceof Player)) return true;
    
        List<String> mobTypeList = config.getStringList("mob-list");
        if(mobTypeList.contains("*")) return false;
    
        EntityType type = enemy.getType();
        String mobType = type.name();
        return !mobTypeList.contains(mobType);
    }
    
    private boolean checkMobSpawnReasonDisabled(LivingEntity enemy) {
        FileConfiguration config = this.expansion.getConfig("mob-tagger.yml");
        List<String> spawnReasonList = config.getStringList("spawn-reason-list");
        if(spawnReasonList.contains("*")) return true;
        
        SpawnReason spawnReason = getSpawnReason(enemy);
        String spawnReasonName = spawnReason.name();
        return spawnReasonList.contains(spawnReasonName);
    }
    
    private SpawnReason getSpawnReason(LivingEntity entity) {
        if(entity == null) return SpawnReason.DEFAULT;
        if(!entity.hasMetadata("combatlogx_spawn_reason")) return SpawnReason.DEFAULT;
    
        List<MetadataValue> spawnReasonValues = entity.getMetadata("combatlogx_spawn_reason");
        for(MetadataValue metadataValue : spawnReasonValues) {
            Object object = metadataValue.value();
            if(!(object instanceof SpawnReason)) continue;
            
            return (SpawnReason) object;
        }
        
        return SpawnReason.DEFAULT;
    }
}