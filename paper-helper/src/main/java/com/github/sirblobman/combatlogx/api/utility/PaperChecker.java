package com.github.sirblobman.combatlogx.api.utility;

import com.github.sirblobman.api.utility.VersionUtility;

public final class PaperChecker {
    private static Boolean USE_PAPER;
    private static Boolean COMPONENT_SUPPORT;

    static {
        USE_PAPER = null;
        COMPONENT_SUPPORT = null;
    }

    public static boolean isPaper() {
        if (USE_PAPER != null) {
            return USE_PAPER;
        }

        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            return (USE_PAPER = true);
        } catch (ReflectiveOperationException | NoClassDefFoundError | ClassCastException ex1) {
            try {
                Class.forName("com.destroystokyo.paper.PaperConfig");
                return (USE_PAPER = true);
            } catch (ReflectiveOperationException | NoClassDefFoundError | ClassCastException ex2) {
                return (USE_PAPER = false);
            }
        }
    }

    /**
     * The server has native component support when {@link org.bukkit.command.CommandSender}
     * is an instance of an {@link net.kyori.adventure.audience.Audience}
     */
    public static boolean hasNativeComponentSupport() {
        if (COMPONENT_SUPPORT != null) {
            return COMPONENT_SUPPORT;
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 16) {
            return (COMPONENT_SUPPORT = false);
        }

        try {
            Class<?> class_CommandSender = Class.forName("org.bukkit.command.CommandSender");
            Class<?> class_Audience = Class.forName("net.kyori.adventure.audience.Audience");

            Class<?>[] class_CommandSender_interfaces = class_CommandSender.getInterfaces();
            for (Class<?> class_CommandSender_interface : class_CommandSender_interfaces) {
                if (class_Audience.equals(class_CommandSender_interface)) {
                    return (COMPONENT_SUPPORT = true);
                }
            }

            return (COMPONENT_SUPPORT = false);
        } catch (ReflectiveOperationException | NoClassDefFoundError | ClassCastException ex) {
            return (COMPONENT_SUPPORT = false);
        }
    }
}
