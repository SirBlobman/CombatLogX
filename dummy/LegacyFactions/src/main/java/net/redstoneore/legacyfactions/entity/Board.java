package net.redstoneore.legacyfactions.entity;

import net.redstoneore.legacyfactions.FLocation;

public abstract class Board {
    public static Board get() {
        throw new UnsupportedOperationException("Dummy Method");
    }

    public abstract Faction getFactionAt(FLocation var1);
}
