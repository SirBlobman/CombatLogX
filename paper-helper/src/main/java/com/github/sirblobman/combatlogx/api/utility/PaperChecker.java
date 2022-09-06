package com.github.sirblobman.combatlogx.api.utility;

import com.github.sirblobman.api.utility.VersionUtility;

public final class PaperChecker {
    private static Boolean USE_PAPER;

    static {
        USE_PAPER = null;
    }


    /**
     * The server is using a valid Paper version when {@link org.bukkit.command.CommandSender}
     * is an instance of an {@link net.kyori.adventure.audience.Audience}
     */
    public static boolean isPaper() {
        if(USE_PAPER != null) {
            return USE_PAPER;
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 16) {
            return (USE_PAPER = false);
        }

        try {
            Class<?> class_CommandSender = Class.forName("org.bukkit.command.CommandSender");
            Class<?> class_Audience = Class.forName("net.kyori.adventure.audience.Audience");

            Class<?>[] class_CommandSender_interfaces = class_CommandSender.getInterfaces();
            for (Class<?> class_CommandSender_interface : class_CommandSender_interfaces) {
                if(class_Audience.equals(class_CommandSender_interface)) {
                    return (USE_PAPER = true);
                }
            }

            return (USE_PAPER = false);
        } catch(ReflectiveOperationException | NoClassDefFoundError | ClassCastException ex) {
            return (USE_PAPER = false);
        }
    }
}
