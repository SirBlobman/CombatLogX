package com.SirBlobman.combatlogx.expansion.compatibility.vanish.hook;

import org.bukkit.entity.Player;

import de.myzelyam.api.vanish.VanishAPI;

public final class HookSuperVanish {
    public static boolean isVanished(Player player) {
        if(player == null) return false;

        return VanishAPI.isInvisible(player);
    }
}