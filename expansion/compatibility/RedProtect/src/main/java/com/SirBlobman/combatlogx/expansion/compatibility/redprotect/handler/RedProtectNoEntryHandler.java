package com.SirBlobman.combatlogx.expansion.compatibility.redprotect.handler;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.redprotect.CompatibilityRedProtect;
import com.SirBlobman.combatlogx.expansion.compatibility.redprotect.hook.HookRedProtect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RedProtectNoEntryHandler extends NoEntryHandler {
    public RedProtectNoEntryHandler(CompatibilityRedProtect expansion) {
        super(expansion);
    }

    @Override
    public String getConfigFileName() {
        return "redprotect-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        return "redprotect-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        return HookRedProtect.isSafeZone(location);
    }
}