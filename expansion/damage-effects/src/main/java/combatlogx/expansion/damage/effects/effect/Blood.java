package combatlogx.expansion.damage.effects.effect;

import java.awt.Color;

import com.github.sirblobman.api.shaded.xseries.particles.Particles;
import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.xseries.particles.ParticleDisplay;
import com.github.sirblobman.api.shaded.xseries.particles.XParticle;

public final class Blood implements DamageEffect, IConfigurable {
    private final Offset offset;
    private boolean enabled;
    private Color color;
    private float size;
    private double ringRate;
    private double ringRadius;
    private double ringTubeRadius;

    public Blood() {
        this.enabled = true;
        this.offset = new Offset();
        this.color = Color.RED;
        this.size = 1.0F;
        this.ringRate = 2.0D;
        this.ringRadius = 2.0D;
        this.ringTubeRadius = 0.5D;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setEnabled(section.getBoolean("enabled", true));
        getOffset().load(getOrCreateSection(section, "offset"));

        int red = section.getInt("color.red", 255);
        int green = section.getInt("color.green", 0);
        int blue = section.getInt("color.blue", 0);
        setColor(red, green, blue);

        setSize((float) section.getDouble("size", 1.0D));
        setRingRate(section.getDouble("ring-rate", 2.0D));
        setRingRadius(section.getDouble("ring-radius", 2.0D));
        setRingTubeRadius(section.getDouble("ring-tube-radius", 0.5D));
    }

    @Override
    public void play(@NotNull Player player) {
        Color color = getColor();
        float size = getSize();
        double rate = getRingRate();
        double radius = getRingRadius();
        double tubeRadius = getRingTubeRadius();

        Offset offset = getOffset();
        double offsetX = offset.getX();
        double offsetY = offset.getY();
        double offsetZ = offset.getZ();
        Location location = player.getLocation().add(offsetX, offsetY, offsetZ);


        ParticleDisplay display = ParticleDisplay.of(XParticle.DUST);
        display.withColor(color, size);
        display.withLocation(location);
        Particles.ring(rate, radius, tubeRadius, display);
    }

    public @NotNull Offset getOffset() {
        return this.offset;
    }

    public @NotNull Color getColor() {
        return color;
    }

    public void setColor(@NotNull Color color) {
        this.color = color;
    }

    public void setColor(int red, int green, int blue) {
        Color color = new Color(red, green, blue);
        setColor(color);
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public double getRingRate() {
        return this.ringRate;
    }

    public void setRingRate(double ringRate) {
        this.ringRate = ringRate;
    }

    public double getRingRadius() {
        return this.ringRadius;
    }

    public void setRingRadius(double ringRadius) {
        this.ringRadius = ringRadius;
    }

    public double getRingTubeRadius() {
        return this.ringTubeRadius;
    }

    public void setRingTubeRadius(double ringTubeRadius) {
        this.ringTubeRadius = ringTubeRadius;
    }
}
