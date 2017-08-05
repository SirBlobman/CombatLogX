package com.SirBlobman.citizens;

import com.SirBlobman.citizens.config.ConfigData;
import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ListenCitizens implements Listener {
    private static List<NPC> NPCS = Util.newList();
    
    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        Location l = p.getLocation();
        String pname = p.getName();
        UntagCause uc = e.getCause();
        if(uc == UntagCause.KICK || uc == UntagCause.QUIT) {
            e.setCancelled(true);
            NPCRegistry reg = CitizensAPI.getNPCRegistry();
            NPC npc = reg.createNPC(EntityType.PLAYER, pname);
            npc.setProtected(false);
            npc.spawn(l);
            NPCS.add(npc);
            Util.runLater(new Runnable() {
                @Override
                public void run() {
                    double health = health(npc);
                    ConfigData.force(p, "health", health);
                    NPCS.remove(npc);
                    npc.destroy();
                }
            }, (60 * 20L));
        }
    }
    
    @EventHandler
    @SuppressWarnings("deprecation")
    public void die(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(NPCS.contains(npc)) {
            String name = npc.getName();
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            ConfigData.force(op, "health", 0.0D);
            NPCS.remove(npc);
            npc.destroy();
        }
    }
    
    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        double health = ConfigData.get(p, "health", p.getHealth());
        if(health == 0.0D) Combat.punish(p);
        else p.setHealth(health);
        ConfigData.remove(p);
    }
    
    private double health(NPC npc) {
        if(npc == null) return 0.0D;
        if(npc.isSpawned()) {
            Entity e = npc.getEntity();
            if(e instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) e;
                double health = le.getHealth();
                return health;
            } else return 0.0D;
        } else return 0.0D;
    }
}