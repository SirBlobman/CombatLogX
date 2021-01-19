package com.github.sirblobman.combatlogx.api.expansion;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.ICombatLogX;

public final class ExpansionLogger extends Logger {
    private final Expansion expansion;
    public ExpansionLogger(Expansion expansion) {
        super(expansion.getName(), null);
        this.expansion = expansion;
    }

    public Expansion getExpansion() {
        return this.expansion;
    }

    @Override
    public void log(LogRecord record) {
        Expansion expansion = getExpansion();
        String expansionPrefix = expansion.getPrefix();

        ICombatLogX combatLogX = expansion.getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();
        String pluginName = plugin.getName();

        String originalMessage = record.getMessage();
        String newMessage = ("[" + expansionPrefix + "] " + originalMessage);
        record.setMessage(newMessage);
        record.setLoggerName(pluginName);

        Logger logger = plugin.getLogger();
        logger.log(record);
    }
}