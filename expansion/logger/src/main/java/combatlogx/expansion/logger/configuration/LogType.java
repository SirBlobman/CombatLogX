package combatlogx.expansion.logger.configuration;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

public enum LogType {
    ENTITY_DAMAGE_EVENT(LogOptions::isLogEntityDamageEvent, LogEntryOptions::getEntityDamageEventFormat),
    PRE_TAG(LogOptions::isLogPreTag, LogEntryOptions::getPretagFormat),
    TAG(LogOptions::isLogTag, LogEntryOptions::getTagFormat),
    RE_TAG(LogOptions::isLogRetag, LogEntryOptions::getRetagFormat),
    UNTAG(LogOptions::isLogUntag, LogEntryOptions::getUntagFormat),
    PUNISH(LogOptions::isLogPunish, LogEntryOptions::getPunishFormat);

    private final Function<LogOptions, Boolean> enabledFunction;
    private final Function<LogEntryOptions, String> formatFunction;

    LogType(@NotNull Function<LogOptions, Boolean> enabledFunction,
            @NotNull Function<LogEntryOptions, String> formatFunction) {
        this.enabledFunction = enabledFunction;
        this.formatFunction = formatFunction;
    }

    private @NotNull Function<LogOptions, Boolean> getEnabledFunction() {
        return this.enabledFunction;
    }

    private @NotNull Function<LogEntryOptions, String> getFormatFunction() {
        return this.formatFunction;
    }

    public boolean isEnabled(LogOptions options) {
        Function<LogOptions, Boolean> function = getEnabledFunction();
        return function.apply(options);
    }

    public String getFormat(LogEntryOptions options) {
        Function<LogEntryOptions, String> formatFunction = getFormatFunction();
        return formatFunction.apply(options);
    }
}
