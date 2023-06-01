package combatlogx.expansion.damage.effects.effect;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class Offset implements IConfigurable {
    private double x;
    private double y;
    private double z;

    public Offset() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setX(section.getDouble("x", 0.0D));
        setY(section.getDouble("y", 0.0D));
        setZ(section.getDouble("z", 0.0D));
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
