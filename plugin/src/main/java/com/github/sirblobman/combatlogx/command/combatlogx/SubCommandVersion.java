package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.update.SpigotUpdateManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextComponent.Builder;
import com.github.sirblobman.api.shaded.adventure.text.format.NamedTextColor;
import com.github.sirblobman.api.shaded.adventure.text.format.TextDecoration;

public final class SubCommandVersion extends CombatLogCommand {
    public SubCommandVersion(@NotNull ICombatLogX plugin) {
        super(plugin, "version");
        setPermissionName("combatlogx.command.combatlogx.version");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessageWithPrefix(sender, "error.console-only");
            return true;
        }

        List<Component> messageList = new ArrayList<>();
        messageList.add(Component.empty());

        addJavaVersionInformation(messageList);
        messageList.add(Component.empty());
        addServerVersionInformation(messageList);
        messageList.add(Component.empty());
        addDependencyInformation(messageList);
        messageList.add(Component.empty());
        addPluginVersionInformation(messageList);
        messageList.add(Component.empty());
        addExpansionInformation(messageList);
        messageList.add(Component.empty());

        LanguageManager languageManager = getLanguageManager();
        for (Component message : messageList) {
            languageManager.sendMessage(sender, message);
        }

        return true;
    }

    private @NotNull Component withPrefix(@NotNull String prefix, @Nullable String value) {
        Builder builder = Component.text();
        builder.append(Component.text(prefix + ":", NamedTextColor.WHITE, TextDecoration.BOLD));

        if (value != null) {
            builder.appendSpace();
            builder.append(Component.text(value, NamedTextColor.GRAY));
        }

        return builder.build();
    }

    private @NotNull Component listElement(@NotNull String value) {
        Builder builder = Component.text();
        builder.append(Component.text(" - ", NamedTextColor.WHITE, TextDecoration.BOLD));
        builder.append(Component.text(value, NamedTextColor.GRAY));
        return builder.build();
    }

    private void addJavaVersionInformation(@NotNull List<Component> messageList) {
        try {
            String javaVersion = System.getProperty("java.version");
            String javaVendor = System.getProperty("java.vendor");
            messageList.add(withPrefix("Java Version", javaVersion));
            messageList.add(withPrefix("Java Vendor", javaVendor));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ex) {
            messageList.add(withPrefix("Java Version", "Unknown"));
        }
    }

    private void addServerVersionInformation(@NotNull List<Component> messageList) {
        String version = Bukkit.getVersion();
        String bukkitVersion = Bukkit.getBukkitVersion();
        String minecraftVersion = VersionUtility.getMinecraftVersion();
        String nmsVersion = VersionUtility.getNetMinecraftServerVersion();

        messageList.add(withPrefix("Server Version", version));
        messageList.add(withPrefix("Bukkit Version", bukkitVersion));
        messageList.add(withPrefix("Minecraft Version", minecraftVersion));
        messageList.add(withPrefix("NMS Version", nmsVersion));
    }

    private void addDependencyInformation(@NotNull List<Component> messageList) {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();
        PluginDescriptionFile information = plugin.getDescription();
        messageList.add(withPrefix("Dependency Information", null));

        List<String> loadBeforeList = information.getLoadBefore();
        List<String> dependList = information.getDepend();
        List<String> softDependList = information.getSoftDepend();

        List<String> fullDependencyList = new ArrayList<>(loadBeforeList);
        fullDependencyList.addAll(dependList);
        fullDependencyList.addAll(softDependList);

        PluginManager pluginManager = Bukkit.getPluginManager();
        for (String dependencyName : fullDependencyList) {
            Plugin dependency = pluginManager.getPlugin(dependencyName);
            if (dependency == null) {
                continue;
            }

            PluginDescriptionFile dependencyInformation = dependency.getDescription();
            String dependencyFullName = dependencyInformation.getFullName();
            messageList.add(listElement(dependencyFullName));
        }
    }

    private void addPluginVersionInformation(@NotNull List<Component> messageList) {
        String pluginVersion = getPluginVersion();
        String spigotVersion = getSpigotVersion();

        messageList.add(Component.text("CombatLogX by SirBlobman", NamedTextColor.WHITE, TextDecoration.BOLD));
        messageList.add(withPrefix("Plugin Version", pluginVersion));
        messageList.add(withPrefix("Spigot Version", spigotVersion));
    }

    private void addExpansionInformation(@NotNull List<Component> messageList) {
        ExpansionManager expansionManager = getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();
        int enabledExpansionListSize = enabledExpansionList.size();

        Builder builder = Component.text();
        builder.append(Component.text("Enabled Expansions (", NamedTextColor.WHITE, TextDecoration.BOLD));
        builder.append(Component.text(enabledExpansionListSize, NamedTextColor.GRAY));
        builder.append(Component.text("):", NamedTextColor.WHITE, TextDecoration.BOLD));
        messageList.add(builder.build());

        for (Expansion expansion : enabledExpansionList) {
            ExpansionDescription description = expansion.getDescription();
            String expansionName = description.getFullName();
            messageList.add(listElement(expansionName));
        }
    }

    private @NotNull String getPluginVersion() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();
        PluginDescriptionFile information = plugin.getDescription();
        return information.getVersion();
    }

    private @NotNull String getSpigotVersion() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();

        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        SpigotUpdateManager updateManager = corePlugin.getSpigotUpdateManager();
        String spigotVersion = updateManager.getSpigotVersion(plugin);
        if (spigotVersion == null) {
            return "Not Available";
        }

        return spigotVersion;
    }
}
