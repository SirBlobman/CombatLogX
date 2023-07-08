package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.configuration.ConfigurationSection;

public final class FlightConfiguration implements IFlightConfiguration {
    private boolean preventFlying;
    private boolean preventFallDamage;
    private boolean forceDisableFlight;
    private boolean flightRetag;

    public FlightConfiguration() {
        this.preventFlying = true;
        this.preventFallDamage = true;
        this.forceDisableFlight = false;
        this.flightRetag = false;
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventFlying(config.getBoolean("prevent-flying", true));
        setPreventFallDamage(config.getBoolean("prevent-fall-damahe", true));
        setForceDisableFlight(config.getBoolean("force-disable-flight", false));
        setFlightRetag(config.getBoolean("flight-retag", false));
    }

    @Override
    public boolean isPreventFlying() {
        return this.preventFlying;
    }

    public void setPreventFlying(boolean preventFlying) {
        this.preventFlying = preventFlying;
    }

    @Override
    public boolean isPreventFallDamage() {
        return this.preventFallDamage;
    }

    public void setPreventFallDamage(boolean preventFallDamage) {
        this.preventFallDamage = preventFallDamage;
    }

    @Override
    public boolean isForceDisableFlight() {
        return this.forceDisableFlight;
    }

    public void setForceDisableFlight(boolean forceDisableFlight) {
        this.forceDisableFlight = forceDisableFlight;
    }

    @Override
    public boolean isFlightRetag() {
        return this.flightRetag;
    }

    public void setFlightRetag(boolean flightRetag) {
        this.flightRetag = flightRetag;
    }
}
