package com.github.sirblobman.combatlogx.api.utility;

import com.github.sirblobman.api.utility.VersionUtility;

public final class PaperChecker {
    /**
     * The server is using a valid Paper version when {@link org.bukkit.command.CommandSender}
     * is an instance of an {@link net.kyori.adventure.audience.Audience}
     */
    public static boolean isPaper() {
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 16) {
            return false;
        }

        try {
            Class<?> class_CommandSender = Class.forName("org.bukkit.command.CommandSender");
            Class<?> class_Audience = Class.forName("net.kyori.adventure.audience.Audience");

            Class<?>[] class_CommandSender_interfaces = class_CommandSender.getInterfaces();
            for (Class<?> class_CommandSender_interface : class_CommandSender_interfaces) {
                if(class_Audience.equals(class_CommandSender_interface)) {
                    return true;
                }
            }

            return false;
        } catch(ReflectiveOperationException | NoClassDefFoundError | ClassCastException ex) {
            return false;
        }
    }
}
