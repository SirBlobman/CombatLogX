package com.SirBlobman.expansion.compatfactions;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatfactions.config.ConfigFactions;
import com.SirBlobman.expansion.compatfactions.config.ConfigFactions.NoEntryMode;
import com.SirBlobman.expansion.compatfactions.util.FactionsUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CompatFactions implements CLXExpansion, Listener {
    public static File FOLDER;
    private static FactionsUtil FUTIL;
    private static List<UUID> MESSAGE_COOLDOWN = Util.newList();

    public String getVersion() {
        return "14.1";
    }

    public String getUnlocalizedName() {
        return "CompatFactions";
    }

    public String getName() {
        return "Factions Compatibility";
    }

    @Override
    public void enable() {
        FUTIL = FactionsUtil.getFactionsUtil();
        if (FUTIL == null) {
            String error = "Could not find a valid Factions plugin. Please contact SirBlobman if you think this should not be happening!";
            print(error);
            Expansions.unloadExpansion(this);
        } else {
            FOLDER = getDataFolder();
            ConfigFactions.load();
            PluginUtil.regEvents(this);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {
        FUTIL = FactionsUtil.getFactionsUtil();
        if (FUTIL != null) {
            FOLDER = getDataFolder();
            ConfigFactions.load();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        if (CombatUtil.isInCombat(player)) {
            if (FUTIL.isSafeZone(to)) {
                Location from = e.getFrom();
                preventEntry(e, player, from, to);
            }
        }
    }

    private void preventEntry(Cancellable e, Player player, Location from, Location to) {
        if (CombatUtil.hasEnemy(player)) {
            LivingEntity enemy = CombatUtil.getEnemy(player);

            NoEntryMode nem = ConfigFactions.getNoEntryMode();
            switch (nem) {
                case CANCEL:
                    e.setCancelled(true);
                    break;
                case TELEPORT:
                    player.teleport(enemy);
                    break;
                case KNOCKBACK:
                    if (!FUTIL.isSafeZone(from)) {
                        Vector v = getVector(from, to);
                        player.setVelocity(v);
                    }
                    break;
                case KILL:
                    player.setHealth(0.0D);
                    break;
            }

            UUID uuid = player.getUniqueId();
            if (!MESSAGE_COOLDOWN.contains(uuid)) {
                String msg = ConfigLang.getWithPrefix("messages.expansions.factions compatibility.no entry");
                player.sendMessage(msg);

                MESSAGE_COOLDOWN.add(uuid);
                SchedulerUtil.runLater(ConfigFactions.MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
            }
        }
    }

    private Vector getVector(Location from, Location to) {
        Vector vfrom = from.toVector();
        Vector vto = to.toVector();
        Vector sub = vfrom.subtract(vto);
        Vector norm = sub.normalize();
        Vector mult = norm.multiply(ConfigFactions.NO_ENTRY_KNOCKBACK_STRENGTH);
        return mult.setY(0.0D);
    }
}