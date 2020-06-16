package com.SirBlobman.combatlogx.expansion.compatibility.citizens.trait;

import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class TraitCombatLogX extends Trait {
    private long ticksUntilRemove;
    private UUID ownerId, enemyId;
    
    private final CompatibilityCitizens expansion;
    public TraitCombatLogX(CompatibilityCitizens expansion) {
        super("combatlogX");
        this.expansion = expansion;
        extendLife();
    }
    
    @Override
    public void run() {
        if(this.ticksUntilRemove > 0) {
            this.ticksUntilRemove--;
            return;
        }
        
        long survivalSeconds = getSurvivalSeconds();
        if(survivalSeconds < 0) return;
        
        Player enemy = getEnemy();
        ICombatLogX plugin = this.expansion.getPlugin();
        if(enemy != null && waitUntilEnemyEscape()) {
            ICombatManager combatManager = plugin.getCombatManager();
            if(combatManager.isInCombat(enemy)) return;
        }
        
        Runnable task = () -> {
            NPC npc = getNPC();
            npc.despawn(DespawnReason.PLUGIN);
        };
    
        JavaPlugin javaPlugin = plugin.getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(javaPlugin, task, 1L);
    }
    
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(this.ownerId);
    }
    
    public void setOwner(OfflinePlayer owner) {
        if(owner == null) return;
        this.ownerId = owner.getUniqueId();
    }
    
    public Player getEnemy() {
        return Bukkit.getPlayer(this.enemyId);
    }
    
    public void setEnemy(Player enemy) {
        if(enemy == null) return;
        this.enemyId = enemy.getUniqueId();
    }
    
    public void extendLife() {
        long survivalSeconds = getSurvivalSeconds();
        this.ticksUntilRemove = (survivalSeconds * 20L);
    }
    
    private long getSurvivalSeconds() {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        return config.getLong("npc-options.survival-seconds", 30L);
    }
    
    private boolean waitUntilEnemyEscape() {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        return config.getBoolean("npc-options.stay-until-enemy-escape", false);
    }
}
