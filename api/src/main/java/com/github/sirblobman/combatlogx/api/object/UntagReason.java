package com.github.sirblobman.combatlogx.api.object;

import java.util.EnumSet;
import java.util.Set;

public enum UntagReason {
    /** The player waited patiently until they were no longer in combat */
    EXPIRE,

    /** The player died and the config option was enabled to untag them */
    SELF_DEATH,

    /** The player's enemy died and the config option was enabled to untag them */
    ENEMY_DEATH,

    /** The player disconnected from the server */
    QUIT,

    /** The player was kicked by a plugin or timed out */
    KICK;

    private static final Set<UntagReason> expireSet = EnumSet.of(EXPIRE, SELF_DEATH, ENEMY_DEATH);
    public boolean isExpire() {
        return expireSet.contains(this);
    }
}
