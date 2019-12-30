package com.SirBlobman.combatlogx.expansion.compatibility.factions.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.CompatibilityFactions;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.FactionsHook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsNoEntryHandler extends NoEntryHandler {
    private final FactionsHook hook;
    public FactionsNoEntryHandler(CompatibilityFactions expansion, FactionsHook hook) {
        super(expansion);
        this.hook = hook;
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
        return this.hook.isSafeZone(location);
    }
}