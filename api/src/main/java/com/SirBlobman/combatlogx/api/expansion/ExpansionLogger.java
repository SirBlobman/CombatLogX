package com.SirBlobman.combatlogx.api.expansion;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;

public class ExpansionLogger extends Logger {
    private final Expansion expansion;
    public ExpansionLogger(Expansion expansion) {
        super(expansion.getName(), null);
        this.expansion = expansion;
    }

    @Override
    public void log(LogRecord record) {
        String message = record.getMessage();
        String prefix = this.expansion.getPrefix();
        String newMessage = (prefix + " " + message);
        record.setMessage(newMessage);

        ICombatLogX plugin = this.expansion.getPlugin();
        Logger logger = plugin.getLogger();
        logger.log(record);
    }
}