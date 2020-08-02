package com.SirBlobman.combatlogx.expansion.compatibility.lands.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.CompatibilityLands;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.hook.HookLands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsNoEntryHandler extends NoEntryHandler {
    private final HookLands hook;
    public LandsNoEntryHandler(CompatibilityLands expansion, HookLands hook) {
        super(expansion);
        this.hook = hook;
    }

    @Override
    public String getConfigFileName() {
        return "lands-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "lands-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        return this.hook.isSafeZone(player, location);
    }
}