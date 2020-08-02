package com.SirBlobman.combatlogx.expansion.compatibility.factions.handler;

import com.SirBlobman.combatlogx.api.shaded.hook.factions.HookFactions;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.CompatibilityFactions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
        HookFactions<?, ?> hookFactions = this.expansion.getHookFactions();
        return hookFactions.isSafeZone(location);
    }
}