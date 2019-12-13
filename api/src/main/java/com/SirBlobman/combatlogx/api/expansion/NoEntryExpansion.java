package com.SirBlobman.combatlogx.api.expansion;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public abstract class NoEntryExpansion extends Expansion {
    public enum NoEntryMode {KILL, CANCEL, TELEPORT, KNOCKBACK, VULNERABLE, NOTHING}
    public NoEntryExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public final void onEnable() {
        if(!canEnable()) {
            Logger logger = getLogger();
            logger.info("Automatically disabling...");

            ExpansionManager.unloadExpansion(this);
            return;
        }

        onActualEnable();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    private final List<UUID> noEntryMessageCooldownList = Util.newList();
    public final void sendNoEntryMessage(Player player, LivingEntity enemy) {
        if(player == null || enemy == null) return;

        UUID uuid = player.getUniqueId();
        if(noEntryMessageCooldownList.contains(uuid)) return;

        ICombatLogX plugin = getPlugin();
        String message = plugin.getLanguageMessageColoredWithPrefix(getNoEntryMessage(enemy instanceof Player ? PlayerPreTagEvent.TagType.PLAYER : PlayerPreTagEvent.TagType.MOB));
        plugin.sendMessage(player, message);

        noEntryMessageCooldownList.add(uuid);

        long delay = (getNoEntryMessageCooldown() * 20L);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLaterAsynchronously(getPlugin().getPlugin(), () -> noEntryMessageCooldownList.remove(uuid), delay);
    }

    public final void preventEntry(Cancellable e, Player player, Location fromLoc, Location toLoc) {
        ICombatManager combatManager = getPlugin().getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        LivingEntity enemy = combatManager.getEnemy(player);
        sendNoEntryMessage(player, enemy);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        NoEntryMode noEntryMode = getNoEntryMode();
        switch(noEntryMode) {
            case KILL:
                player.setHealth(0.0D);
                break;

            case CANCEL:
                e.setCancelled(true);
                break;

            case TELEPORT:
                if(enemy != null) player.teleport(enemy);
                else e.setCancelled(true);
                break;

            case KNOCKBACK:
                e.setCancelled(true);
                scheduler.runTaskLater(getPlugin().getPlugin(), () -> knockbackPlayer(player, fromLoc, toLoc), 1L);
                break;

            case VULNERABLE:
            case NOTHING:
            default:
                break;
        }
    }

    private void knockbackPlayer(Player player, Location fromLoc, Location toLoc) {
        if(player == null) return;

        Vector vector = getVector(fromLoc, toLoc);
        player.setVelocity(vector);
    }

    private Vector getVector(Location fromLoc, Location toLoc) {
        if(fromLoc == null || toLoc == null) return null;

        Vector fromVec = fromLoc.toVector();
        Vector toVec = toLoc.toVector();

        Vector subtract = fromVec.subtract(toVec);
        Vector normal = subtract.normalize();
        Vector multiply = normal.multiply(getNoEntryKnockbackStrength());
        return makeFinite(multiply);
    }

    private Vector makeFinite(Vector original) {
        if(original == null) return null;

        double x = makeFinite(original.getX());
        double y = makeFinite(original.getY());
        double z = makeFinite(original.getZ());
        return new Vector(x, y, z);
    }

    private double makeFinite(double original) {
        if(Double.isNaN(original)) return 0.0D;
        if(Double.isInfinite(original)) return (original < 0.0D ? -1.0D : 1.0D);

        return original;
    }

    public abstract boolean canEnable();
    public abstract void onActualEnable();

    public abstract double getNoEntryKnockbackStrength();
    public abstract NoEntryMode getNoEntryMode();
    public abstract String getNoEntryMessage(PlayerPreTagEvent.TagType tagType);
    public abstract int getNoEntryMessageCooldown();
}