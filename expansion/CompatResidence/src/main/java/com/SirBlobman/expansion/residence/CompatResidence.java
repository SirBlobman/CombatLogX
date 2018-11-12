package com.SirBlobman.expansion.residence;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.residence.config.ConfigResidence;
import com.SirBlobman.expansion.residence.config.ConfigResidence.NoEntryMode;
import com.SirBlobman.expansion.residence.utility.ResidenceUtil;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class CompatResidence implements CLXExpansion, Listener {
    public String getUnlocalizedName() {return "CompatResidence";}
    public String getName() {return "Residence Compatibility";}
    public String getVersion() {return "13.1";}
    
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(PluginUtil.isEnabled("Residence", "bekvon")) {
            FOLDER = getDataFolder();
            ConfigResidence.load();
            PluginUtil.regEvents(this);
        } else {
            String error = "Could not find Residence. Automatically disabling...";
            Expansions.unloadExpansion(this);
            print(error);
        }
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void onConfigReload() {
        
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (CombatUtil.isInCombat(player)) {
            Location to = e.getTo().clone();
            Location from = e.getFrom().clone();
            if (ResidenceUtil.isSafeZone(to)) preventEntry(e, player, from, to);
        }
    }

    private static List<UUID> MESSAGE_COOLDOWN = Util.newList();
    private void preventEntry(Cancellable e, Player player, Location from, Location to) {
        if (CombatUtil.hasEnemy(player)) {
            NoEntryMode nem = ConfigResidence.getNoEntryMode();
            switch (nem) {
                case CANCEL:
                    e.setCancelled(true);
                    break;
                case TELEPORT:
                    LivingEntity enemy = CombatUtil.getEnemy(player);
                    player.teleport(enemy);
                    break;
                case KNOCKBACK:
                    if (!ResidenceUtil.isSafeZone(from)) {
                        Vector knockback = getVector(from, to);
                        player.setVelocity(knockback);
                    }
                    break;
                case KILL:
                    player.setHealth(0.0D);
                    break;
            }

            UUID uuid = player.getUniqueId();
            if (!MESSAGE_COOLDOWN.contains(uuid)) {
                String msg = ConfigLang.getWithPrefix("messages.expansions.residence compatibility.no entry");
                player.sendMessage(msg);

                MESSAGE_COOLDOWN.add(uuid);
                SchedulerUtil.runLater(ConfigResidence.NO_ENTRY_MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
            }
        }
    }

    private Vector getVector(Location from, Location to) {
        Vector vfrom = from.toVector();
        Vector vto = to.toVector();
        Vector sub = vfrom.subtract(vto);
        Vector norm = sub.normalize();
        Vector mult = norm.multiply(ConfigResidence.NO_ENTRY_KNOCKBACK_STRENGTH);
        return mult.setY(0.0D);
    }
}