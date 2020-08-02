package com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.CompatibilityPreciousStones;
import com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.hook.HookPreciousStones;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PreciousStonesNoEntryHandler extends NoEntryHandler {
    public PreciousStonesNoEntryHandler(CompatibilityPreciousStones expansion) {
        super(expansion);
    }

    @Override
    public String getConfigFileName() {
        return "preciousstones-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "preciousstones-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        return HookPreciousStones.isSafeZone(location);
    }
}