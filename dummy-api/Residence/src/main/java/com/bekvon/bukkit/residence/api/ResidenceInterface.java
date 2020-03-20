package com.bekvon.bukkit.residence.api;

import org.bukkit.Location;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public interface ResidenceInterface {
    ClaimedResidence getByLoc(Location location);
}