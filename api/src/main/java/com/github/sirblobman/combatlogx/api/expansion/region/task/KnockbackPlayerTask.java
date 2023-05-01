package com.github.sirblobman.combatlogx.api.expansion.region.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

public final class KnockbackPlayerTask extends EntityTaskDetails<Player> {
    private final Location from;
    private final Location to;
    private final double strength;

    public KnockbackPlayerTask(@NotNull ICombatLogX plugin, @NotNull Player entity,
                               @NotNull Location from, @NotNull Location to, double strength) {
        super(plugin.getPlugin(), entity);
        this.from = from;
        this.to = to;
        this.strength = strength;
    }

    private @NotNull Location getFrom() {
        return this.from;
    }

    private @NotNull Location getTo() {
        return this.to;
    }

    private double getKnockbackStrength() {
        return this.strength;
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player == null) {
            return;
        }

        Location from = getFrom();
        Location to = getTo();
        knockbackPlayer(player, from, to);
    }

    private void knockbackPlayer(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        Vector velocity = getKnockback(from, to);
        player.setVelocity(velocity);
    }

    private @NotNull Vector getKnockback(@NotNull Location from, @NotNull Location to) {
        Vector fromVector = from.toVector();
        Vector toVector = to.toVector();

        Vector subtract = fromVector.subtract(toVector);
        Vector normal = subtract.normalize();

        double strength = getKnockbackStrength();
        Vector multiply = normal.multiply(strength);

        return makeFinite(multiply);
    }

    private @NotNull Vector makeFinite(@NotNull Vector original) {
        double originalX = original.getX();
        double originalY = original.getY();
        double originalZ = original.getZ();

        double newX = makeFinite(originalX);
        double newY = makeFinite(originalY);
        double newZ = makeFinite(originalZ);
        return new Vector(newX, newY, newZ);
    }

    private double makeFinite(double original) {
        if (Double.isNaN(original)) {
            return 0.0D;
        }

        if (Double.isInfinite(original)) {
            return (original < 0 ? -1.0D : 1.0D);
        }

        return original;
    }
}
