package com.SirBlobman.combatlogx.expansion.compatibility.griefprevention.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.griefprevention.CompatibilityGriefPrevention;
import com.SirBlobman.combatlogx.expansion.compatibility.griefprevention.hook.HookGriefPrevention;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionNoEntryHandler extends NoEntryHandler {
    public GriefPreventionNoEntryHandler(CompatibilityGriefPrevention expansion) {
        super(expansion);
    }

    @Override
    public String getConfigFileName() {
        return "griefprevention-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "griefprevention-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        return HookGriefPrevention.isSafeZone(location);
    }
}