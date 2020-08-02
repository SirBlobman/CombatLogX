package com.SirBlobman.combatlogx.api.expansion.noentry;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public abstract class NoEntryExpansion extends Expansion {
    private boolean actuallyEnabled = false;
    public NoEntryExpansion(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public final void onEnable() {
        if(!canEnable()) {
            Logger logger = getLogger();
            logger.info("Automatically disabling...");
            
            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        onActualEnable();
        this.actuallyEnabled = true;
    }

    @Override
    public final void onDisable() {
        if(!actuallyEnabled) return;
        
        onActualDisable();
        this.actuallyEnabled = false;
    }

    private final List<UUID> noEntryMessageCooldownList = Util.newList();
    public final void sendNoEntryMessage(Player player, LivingEntity enemy) {
        if(player == null || enemy == null) return;

        UUID uuid = player.getUniqueId();
        if(noEntryMessageCooldownList.contains(uuid)) return;

        NoEntryHandler handler = getNoEntryHandler();
        TagType tagType = (enemy instanceof Player ? TagType.PLAYER : TagType.MOB);
        String messagePath = handler.getNoEntryMessagePath(tagType);

        ICombatLogX plugin = getPlugin();
        String message = plugin.getLanguageMessageColoredWithPrefix(messagePath);
        plugin.sendMessage(player, message);

        noEntryMessageCooldownList.add(uuid);

        long cooldown = handler.getNoEntryMessageCooldown();
        long delay = (cooldown * 20L);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLaterAsynchronously(plugin.getPlugin(), () -> noEntryMessageCooldownList.remove(uuid), delay);
    }

    public final void preventEntry(Cancellable e, Player player, Location fromLoc, Location toLoc) {
        ICombatManager combatManager = getPlugin().getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        LivingEntity enemy = combatManager.getEnemy(player);
        sendNoEntryMessage(player, enemy);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        NoEntryHandler handler = getNoEntryHandler();
        NoEntryMode noEntryMode = handler.getNoEntryMode();
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

        NoEntryHandler handler = getNoEntryHandler();
        double strength = handler.getNoEntryKnockbackStrength();
        Vector multiply = normal.multiply(strength);

        return makeFinite(multiply);
    }

    private Vector makeFinite(Vector original) {
        if(original == null) return null;

        double ox = original.getX(), fx = makeFinite(ox);
        double oy = original.getY(), fy = makeFinite(oy);
        double oz = original.getZ(), fz = makeFinite(oz);
        return new Vector(fx, fy, fz);
    }

    private double makeFinite(double original) {
        if(Double.isNaN(original)) return 0.0D;
        if(Double.isInfinite(original)) return (original < 0.0D ? -1.0D : 1.0D);

        return original;
    }

    public abstract boolean canEnable();
    public abstract void onActualEnable();
    public abstract void onActualDisable();
    public abstract NoEntryHandler getNoEntryHandler();
}