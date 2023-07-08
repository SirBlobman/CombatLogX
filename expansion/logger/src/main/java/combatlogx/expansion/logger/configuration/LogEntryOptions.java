package combatlogx.expansion.logger.configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class LogEntryOptions implements IConfigurable {
    private String prefixFormat;
    private String entityDamageEventFormat;
    private String pretagFormat;
    private String tagFormat;
    private String retagFormat;
    private String untagFormat;
    private String punishFormat;

    private transient DateFormat dateFormat;

    public LogEntryOptions() {
        this.prefixFormat = "";
        this.entityDamageEventFormat = "";
        this.pretagFormat = "";
        this.tagFormat = "";
        this.retagFormat = "";
        this.untagFormat = "";
        this.punishFormat = "";

        this.dateFormat = null;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
    }

    public @NotNull String getPrefixFormat() {
        return this.prefixFormat;
    }

    public void setPrefixFormat(@NotNull String prefixFormat) {
        this.prefixFormat = prefixFormat;
    }

    public @NotNull String getEntityDamageEventFormat() {
        return this.entityDamageEventFormat;
    }

    public void setEntityDamageEventFormat(@NotNull String entityDamageEventFormat) {
        this.entityDamageEventFormat = entityDamageEventFormat;
    }

    public @NotNull String getPretagFormat() {
        return this.pretagFormat;
    }

    public void setPretagFormat(@NotNull String pretagFormat) {
        this.pretagFormat = pretagFormat;
    }

    public @NotNull String getTagFormat() {
        return this.tagFormat;
    }

    public void setTagFormat(@NotNull String tagFormat) {
        this.tagFormat = tagFormat;
    }

    public @NotNull String getRetagFormat() {
        return this.retagFormat;
    }

    public void setRetagFormat(@NotNull String retagFormat) {
        this.retagFormat = retagFormat;
    }

    public @NotNull String getUntagFormat() {
        return this.untagFormat;
    }

    public void setUntagFormat(@NotNull String untagFormat) {
        this.untagFormat = untagFormat;
    }

    public @NotNull String getPunishFormat() {
        return this.punishFormat;
    }

    public void setPunishFormat(@NotNull String punishFormat) {
        this.punishFormat = punishFormat;
    }

    public @NotNull DateFormat getDateFormat() {
        if (this.dateFormat != null) {
            return this.dateFormat;
        }

        String prefixFormat = getPrefixFormat();
        this.dateFormat = new SimpleDateFormat(prefixFormat);
        return this.dateFormat;
    }

    public @NotNull String getCurrentPrefix() {
        Instant now = Instant.now();
        Date date = Date.from(now);
        DateFormat dateFormat = getDateFormat();
        return dateFormat.format(date);
    }
}
