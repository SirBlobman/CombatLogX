package com.SirBlobman.expansion.lands;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.lands.config.ConfigLands;
import com.SirBlobman.expansion.lands.config.ConfigLands.NoEntryMode;
import com.SirBlobman.expansion.lands.utility.LandsUtil;

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

public class CompatLands implements CLXExpansion, Listener {
    public static File FOLDER;
    private static List<UUID> MESSAGE_COOLDOWN = Util.newList();

    public String getUnlocalizedName() {
        return "CompatLands";
    }

    public String getName() {
        return "Lands Compatibility";
    }

    public String getVersion() {
        return "13.1";
    }

    @Override
    public void enable() {
        if (PluginUtil.isEnabled("Lands", "Angeschossen")) {
            FOLDER = getDataFolder();
            ConfigLands.load();
            PluginUtil.regEvents(this);
        } else {
            String error = "Lands is not installed. Automatically disabling...";
            print(error);
            Expansions.unloadExpansion(this);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {
        if (PluginUtil.isEnabled("Lands", "Angeschossen")) ConfigLands.load();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnterLand(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (CombatUtil.isInCombat(player)) {
            Location to = e.getTo().clone();
            Location from = e.getFrom().clone();
            if (LandsUtil.isSafeZone(to)) preventEntry(e, player, from, to);
        }
    }

    private void preventEntry(Cancellable e, Player player, Location from, Location to) {
        if (CombatUtil.hasEnemy(player)) {
            NoEntryMode nem = ConfigLands.getNoEntryMode();
            switch (nem) {
                case CANCEL:
                    e.setCancelled(true);
                    break;
                case TELEPORT:
                    LivingEntity enemy = CombatUtil.getEnemy(player);
                    player.teleport(enemy);
                    break;
                case KNOCKBACK:
                    if (!LandsUtil.isSafeZone(from)) {
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
                String msg = ConfigLang.getWithPrefix("messages.expansions.lands compatibility.no entry");
                player.sendMessage(msg);

                MESSAGE_COOLDOWN.add(uuid);
                SchedulerUtil.runLater(ConfigLands.NO_ENTRY_MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
            }
        }
    }

    private Vector getVector(Location from, Location to) {
        Vector vfrom = from.toVector();
        Vector vto = to.toVector();
        Vector sub = vfrom.subtract(vto);
        Vector norm = sub.normalize();
        Vector mult = norm.multiply(ConfigLands.NO_ENTRY_KNOCKBACK_STRENGTH);
        return mult.setY(0.0D);
    }
}