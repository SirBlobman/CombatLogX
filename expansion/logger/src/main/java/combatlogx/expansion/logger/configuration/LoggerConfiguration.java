package combatlogx.expansion.logger.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class LoggerConfiguration implements IConfigurable {
    private final LogFileInfo logFileInfo;
    private final LogOptions logOptions;
    private final LogEntryOptions logEntryOptions;

    public LoggerConfiguration() {
        this.logFileInfo = new LogFileInfo();
        this.logOptions = new LogOptions();
        this.logEntryOptions = new LogEntryOptions();
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        getLogFileInfo().load(getOrCreateSection(config, "log-file-info"));
        getLogOptions().load(getOrCreateSection(config, "log-options"));
        getLogEntryOptions().load(getOrCreateSection(config, "log-entry-options"));
    }

    public LogFileInfo getLogFileInfo() {
        return this.logFileInfo;
    }

    public LogOptions getLogOptions() {
        return this.logOptions;
    }

    public LogEntryOptions getLogEntryOptions() {
        return this.logEntryOptions;
    }
}
