package com.github.sirblobman.combatlogx.api.object;

public enum TimerType {
    /**
     * Every player will be tagged for the same amount of time
     */
    GLOBAL,
    
    /**
     * Some players will have special combat times based on their permissions, others will use the global time
     */
    PERMISSION;
    
    /**
     * @param string The string to parse.
     * @return A {@link TimerType} that matches the uppercase value of the string or {@link #GLOBAL} if one could not be
     * matched.
     */
    public static TimerType parse(String string) {
        try {
            String value = string.toUpperCase();
            return valueOf(value);
        } catch(IllegalArgumentException | NullPointerException ex) {
            return GLOBAL;
        }
    }
}
