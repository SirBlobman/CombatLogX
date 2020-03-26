package net.sacredlabyrinth.Phaed.PreciousStones.api;

import org.bukkit.Location;

import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;

public interface IApi {
    boolean isFieldProtectingArea(FieldFlag flag, Location location);
}