package com.github.sirblobman.combatlogx.api.object;

public enum UntagReason {
    /**
     * The player waited patiently until they were no longer in combat
     */
    EXPIRE(true),
    
    /**
     * The player died and the config option was enabled to untag them
     */
    SELF_DEATH(true),
    
    /**
     * The player's enemy died and the config option was enabled to untag them
     */
    ENEMY_DEATH(true),
    
    /**
     * The player disconnected from the server
     */
    QUIT,
    
    /**
     * The player was kicked by a plugin or timed out
     */
    KICK;
    
    private final boolean expire;
    
    UntagReason() {
        this(false);
    }
    
    UntagReason(boolean expire) {
        this.expire = expire;
    }
    
    public boolean isExpire() {
        return this.expire;
    }
}
