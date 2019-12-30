package com.SirBlobman.combatlogx.expansion.compatibility.hook;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public final class HookBukkitVanish {
    public static boolean isVanished(Player player) {
        if(player == null) return false;

        List<MetadataValue> metaList = player.getMetadata("vanished");
        for(MetadataValue meta : metaList) {
            boolean value = meta.asBoolean();
            if(value) return true;
        }

        return false;
    }
}