package com.github.sirblobman.combatlogx.api.object;

public enum TagType {
    /** Unknown type for being tagged.
     * Usually occurs in the `Damage Tagger` expansion or from the tag command.
     */
    UNKNOWN,

    /** Tag was caused by another player or themselves */
    PLAYER,

    /** Tag was caused by a mob */
    MOB;
}
