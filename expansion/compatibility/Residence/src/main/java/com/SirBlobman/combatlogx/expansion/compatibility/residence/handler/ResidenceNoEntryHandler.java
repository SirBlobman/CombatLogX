package com.SirBlobman.combatlogx.expansion.compatibility.residence.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.residence.CompatibilityResidence;
import com.SirBlobman.combatlogx.expansion.compatibility.residence.hook.HookResidence;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceNoEntryHandler extends NoEntryHandler {
    public ResidenceNoEntryHandler(CompatibilityResidence expansion) {
        super(expansion);
    }

    @Override
    public String getConfigFileName() {
        return "residence-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "residence-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        return HookResidence.isSafeZone(location);
    }
}