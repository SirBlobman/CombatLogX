package com.SirBlobman.expansion.compatibility.disguise.hook;

import org.bukkit.entity.Player;

import me.libraryaddict.disguise.DisguiseAPI;

public final class HookLibsDisguises {
    public static boolean isDisguised(Player player) {
        return DisguiseAPI.isDisguised(player);
    }

    public static void undisguise(Player player) {
        DisguiseAPI.undisguiseToAll(player);
    }
}
