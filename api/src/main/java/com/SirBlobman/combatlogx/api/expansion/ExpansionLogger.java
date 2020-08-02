package com.SirBlobman.combatlogx.api.expansion;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;

public class ExpansionLogger extends Logger {
    public ExpansionLogger(Expansion expansion) {
        super(expansion.getDescription().getName(), null);
        
        ICombatLogX combat = expansion.getPlugin();
        Logger parent = combat.getLogger();
        
        setParent(parent);
        setLevel(Level.ALL);
    }
}