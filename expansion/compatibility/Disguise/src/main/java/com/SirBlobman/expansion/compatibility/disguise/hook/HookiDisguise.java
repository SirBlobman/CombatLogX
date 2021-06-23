package com.SirBlobman.expansion.compatibility.disguise.hook;

import org.bukkit.entity.Player;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.iDisguise;

public final class HookiDisguise {
    private static DisguiseAPI getAPI() {
        iDisguise disguisePlugin = iDisguise.getInstance();
        return disguisePlugin.getAPI();
    }

    public static boolean isDisguised(Player player) {
        DisguiseAPI api = getAPI();
        return api.isDisguised(player);
    }

    public static void undisguise(Player player) {
        DisguiseAPI api = getAPI();
        api.undisguise(player, false);
    }
}
