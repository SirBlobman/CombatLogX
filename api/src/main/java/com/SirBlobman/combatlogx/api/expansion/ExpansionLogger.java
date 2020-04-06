package com.SirBlobman.combatlogx.api.expansion;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;

public class ExpansionLogger extends Logger {
    private final String expansionPrefix;
    private final Expansion expansion;
    public ExpansionLogger(Expansion expansion) {
        super(expansion.getClass().getCanonicalName(), null);
        this.expansion = expansion;
    
        ExpansionDescription description = expansion.getDescription();
        String displayName = description.getDisplayName();
        this.expansionPrefix = ("[" + displayName + "] ");
    }
    
    @Override
    public void log(LogRecord record) {
        ICombatLogX plugin = this.expansion.getPlugin();
        Logger logger = plugin.getLogger();
        
        String originalMessage = record.getMessage();
        record.setMessage(this.expansionPrefix + originalMessage);
        logger.log(record);
    }
}