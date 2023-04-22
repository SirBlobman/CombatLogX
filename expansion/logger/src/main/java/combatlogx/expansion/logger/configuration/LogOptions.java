package combatlogx.expansion.logger.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class LogOptions implements IConfigurable {
    private boolean logEntityDamageEvent;
    private boolean logPreTag;
    private boolean logTag;
    private boolean logRetag;
    private boolean logUntag;
    private boolean logPunish;

    public LogOptions() {
        this.logEntityDamageEvent = true;
        this.logPreTag = true;
        this.logTag = true;
        this.logRetag = true;
        this.logUntag = true;
        this.logPunish = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setLogEntityDamageEvent(section.getBoolean("log-entity-damage-event", true));
        setLogPreTag(section.getBoolean("log-pretag", true));
        setLogTag(section.getBoolean("log-tag", true));
        setLogRetag(section.getBoolean("log-retag", true));
        setLogUntag(section.getBoolean("log-untag", true));
        setLogPunish(section.getBoolean("log-punish", true));
    }

    public boolean isLogEntityDamageEvent() {
        return this.logEntityDamageEvent;
    }

    public void setLogEntityDamageEvent(boolean logEntityDamageEvent) {
        this.logEntityDamageEvent = logEntityDamageEvent;
    }

    public boolean isLogPreTag() {
        return this.logPreTag;
    }

    public void setLogPreTag(boolean logPreTag) {
        this.logPreTag = logPreTag;
    }

    public boolean isLogTag() {
        return this.logTag;
    }

    public void setLogTag(boolean logTag) {
        this.logTag = logTag;
    }

    public boolean isLogRetag() {
        return this.logRetag;
    }

    public void setLogRetag(boolean logRetag) {
        this.logRetag = logRetag;
    }

    public boolean isLogUntag() {
        return this.logUntag;
    }

    public void setLogUntag(boolean logUntag) {
        this.logUntag = logUntag;
    }

    public boolean isLogPunish() {
        return this.logPunish;
    }

    public void setLogPunish(boolean logPunish) {
        this.logPunish = logPunish;
    }
}
