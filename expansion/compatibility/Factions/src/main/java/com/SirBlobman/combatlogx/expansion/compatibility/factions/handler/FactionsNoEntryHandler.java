package com.SirBlobman.combatlogx.expansion.compatibility.factions.handler;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.factions.FactionsHandler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.CompatibilityFactions;

public class FactionsNoEntryHandler extends NoEntryHandler {
    private final CompatibilityFactions expansion;
    public FactionsNoEntryHandler(CompatibilityFactions expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @Override
    public String getConfigFileName() {
        return "factions-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "factions-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        FactionsHandler factionsHandler = this.expansion.getFactionsHandler();
        return factionsHandler.isSafeZone(location);
    }
}
