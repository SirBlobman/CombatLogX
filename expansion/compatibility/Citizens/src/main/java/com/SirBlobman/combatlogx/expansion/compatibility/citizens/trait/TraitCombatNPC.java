package com.SirBlobman.combatlogx.expansion.compatibility.citizens.trait;

import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility.NPCManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;

public class TraitCombatNPC extends Trait {
    public static TraitInfo traitInfo;
    private static Expansion expansion;
    public static void onEnable(Expansion exp) {
        expansion = exp;
        traitInfo = TraitInfo.create(TraitCombatNPC.class);
        CitizensAPI.getTraitFactory().registerTrait(traitInfo);
    }

    public static void onDisable() {
        
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        for(NPC npc : registry) {
            if(NPCManager.isInvalid(npc)) continue;
            npc.destroy();
        }

        CitizensAPI.getTraitFactory().deregisterTrait(traitInfo);
    }

    private long ticksUntilRemove;
    private UUID ownerUUID, enemyUUID;
    public TraitCombatNPC() {
        super("combatlogx");
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(this.ownerUUID);
    }

    public void setOwner(OfflinePlayer owner) {
        if(owner == null) return;
        this.ownerUUID = owner.getUniqueId();
    }

    public Player getEnemy() {
        return Bukkit.getPlayer(this.enemyUUID);
    }

    public void setEnemy(Player enemy) {
        if(enemy == null) return;
        this.enemyUUID = enemy.getUniqueId();
    }

    public String getId() {
        String npcName = this.npc.getName();
        int npcId = this.npc.getId();
        return (npcName + ":" + npcId);
    }

    public void extendTimeUntilRemove() {
        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        long survivalSeconds = config.getLong("npc-options.survival-seconds", 30L);
        this.ticksUntilRemove = (20L * survivalSeconds);
    }

    @Override
    public void onAttach() {
        extendTimeUntilRemove();
    }

    @Override
    public void run() {
        if(this.ticksUntilRemove > 0) {
            this.ticksUntilRemove--;
            return;
        }

        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        long survivalSeconds = config.getLong("npc-options.survival-seconds", 30L);
        if(survivalSeconds < 0) return;

        if(config.getBoolean("npc-options.stay-until-enemy-escape")) {
            Player enemy = getEnemy();
            if(enemy == null) return;

            ICombatLogX plugin = expansion.getPlugin();
            ICombatManager manager = plugin.getCombatManager();
            if(manager.isInCombat(enemy)) return;
        }

        Runnable task = () -> this.npc.despawn(DespawnReason.PLUGIN);
        JavaPlugin plugin = expansion.getPlugin().getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, task, 1L);
    }
}