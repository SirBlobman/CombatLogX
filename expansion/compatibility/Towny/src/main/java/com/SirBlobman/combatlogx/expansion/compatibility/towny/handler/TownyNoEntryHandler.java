package com.SirBlobman.combatlogx.expansion.compatibility.towny.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.towny.CompatibilityTowny;
import com.SirBlobman.combatlogx.expansion.compatibility.towny.hook.HookTowny;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyNoEntryHandler extends NoEntryHandler {
    public TownyNoEntryHandler(CompatibilityTowny expansion) {
        super(expansion);
    }

    @Override
    public String getConfigFileName() {
        return "towny-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "towny-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        return HookTowny.isSafeZone(location);
    }
}