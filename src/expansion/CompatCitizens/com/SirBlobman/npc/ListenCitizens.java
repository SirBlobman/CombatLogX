package com.SirBlobman.npc;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.npc.config.ConfigCitizens;
import com.SirBlobman.npc.config.ConfigData;
import com.SirBlobman.npc.utility.NPCUtil;
import com.SirBlobman.npc.utility.NPCUtil.CombatNPC;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.UUID;

import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;

public class ListenCitizens implements Listener {
    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        Location l = p.getLocation();
        UntagCause uc = e.getCause();
        if (uc == UntagCause.KICK || uc == UntagCause.QUIT) {
            e.setCancelled(true);
            NPCUtil.createNPC(p, l);
        }
    }

    @EventHandler
    public void die(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if (npc.hasTrait(CombatNPC.class))
            NPCUtil.removeNPC(npc);
        else
            return;
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        NPCUtil.removeNPC(uuid);

        if (ConfigData.exists(p)) {
            double health = ConfigData.get(p, "health", p.getHealth());
            Location l = ConfigData.get(p, "last location", p.getLocation());
            if (health == 0.0D) {
                p.setHealth(0.0D);
                Combat.punish(p);
            } else {
                p.setHealth(health);
                p.teleport(l);
                World world = p.getWorld();

                if (ConfigCitizens.OPTION_MODIFY_INVENTORIES) {
                    List<ItemStack> list = ConfigData.get(p, "inventory", Util.newList());
                    PlayerInventory pi = p.getInventory();
                    for (ItemStack is : list) {
                        int first = pi.firstEmpty();
                        if (first >= 0)
                            pi.addItem(is);
                        else
                            world.dropItem(l, is);
                    }
                }
            }
            ConfigData.remove(p);
        }
    }
}