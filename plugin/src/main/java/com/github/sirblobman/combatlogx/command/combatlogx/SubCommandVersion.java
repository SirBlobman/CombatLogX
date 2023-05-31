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
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.SpigotUpdateManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextComponent;
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
        TextComponent.Builder builder = Component.text().color(NamedTextColor.WHITE);
        builder.append(Component.text(prefix).decorate(TextDecoration.BOLD));
        builder.append(Component.text(":").decorate(TextDecoration.BOLD));
        builder.appendSpace();

        if (value != null) {
            builder.append(Component.text(value, NamedTextColor.GRAY));
        } else {
            builder.append(Component.text("N/A", NamedTextColor.GRAY));
        }

        return builder.build();
    }

    private @NotNull Component listElement(@NotNull String value) {
        TextComponent.Builder builder = Component.text().color(NamedTextColor.GRAY);
        builder.append(Component.text(" - ", NamedTextColor.WHITE, TextDecoration.BOLD));
        builder.append(Component.text(value));
        return builder.build();
    }

    private void addJavaVersionInformation(@NotNull List<Component> list) {
        String javaVersion = getProperty("java.version");
        String javaVendor = getProperty("java.vendor");
        String javaURL = getProperty("java.url");
        list.add(withPrefix("Java Version", javaVersion));
        list.add(withPrefix("Java Vendor", javaVendor));
        list.add(withPrefix("Java URL", javaURL));
    }

    private @NotNull String getProperty(String name) {
        try {
            return System.getProperty(name);
        } catch (SecurityException | IllegalArgumentException | NullPointerException ex) {
            return "Error";
        }
    }

    private void addServerVersionInformation(@NotNull List<Component> list) {
        String version = Bukkit.getVersion();
        String bukkitVersion = Bukkit.getBukkitVersion();
        String minecraftVersion = VersionUtility.getMinecraftVersion();
        String nmsVersion = VersionUtility.getNetMinecraftServerVersion();

        list.add(withPrefix("Server Version", version));
        list.add(withPrefix("Bukkit Version", bukkitVersion));
        list.add(withPrefix("Minecraft Version", minecraftVersion));
        list.add(withPrefix("NMS Version", nmsVersion));
    }

    private void addDependencyInformation(@NotNull List<Component> list) {
        ICombatLogX combatLogX = getCombatLogX();
        ConfigurablePlugin plugin = combatLogX.getPlugin();
        PluginDescriptionFile description = plugin.getDescription();
        list.add(Component.text("Dependency Information:", NamedTextColor.WHITE, TextDecoration.BOLD));

        List<String> loadBeforeList = description.getLoadBefore();
        List<String> softDependList = description.getSoftDepend();
        List<String> dependList = description.getDepend();

        List<String> fullDependencyList = new ArrayList<>(loadBeforeList);
        fullDependencyList.addAll(softDependList);
        fullDependencyList.addAll(dependList);

        if (fullDependencyList.isEmpty()) {
            list.add(listElement("None"));
            return;
        }

        Component missingText = Component.text("(not installed)", NamedTextColor.RED);
        PluginManager pluginManager = Bukkit.getPluginManager();
        for (String dependencyName : fullDependencyList) {
            Plugin dependency = pluginManager.getPlugin(dependencyName);
            if(dependency == null) {
                list.add(listElement(dependencyName).append(Component.space()).append(missingText));
                continue;
            }

            PluginDescriptionFile dependencyDescription = dependency.getDescription();
            String dependencyFullName = dependencyDescription.getFullName();
            list.add(listElement(dependencyFullName));
        }
    }

    private void addPluginVersionInformation(@NotNull List<Component> list) {
        String localVersion = getPluginVersion();
        String remoteVersion = getRemoteVersion();

        list.add(Component.text("CombatLogX by SirBlobman", NamedTextColor.WHITE, TextDecoration.BOLD));
        list.add(withPrefix("Local Version", localVersion));
        list.add(withPrefix("Remote Version", remoteVersion));
    }

    private void addExpansionInformation(@NotNull List<Component> list) {
        ExpansionManager expansionManager = getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();
        int enabledExpansionListSize = enabledExpansionList.size();

        Builder builder = Component.text().color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD);
        builder.append(Component.text("Enabled Expansions ("));
        builder.append(Component.text(enabledExpansionListSize, NamedTextColor.GRAY));
        builder.append(Component.text("):"));
        list.add(builder.build());

        for (Expansion expansion : enabledExpansionList) {
            ExpansionDescription description = expansion.getDescription();
            String expansionName = description.getFullName();
            list.add(listElement(expansionName));
        }
    }

    private @NotNull String getPluginVersion() {
        ICombatLogX combatLogX = getCombatLogX();
        ConfigurablePlugin plugin = combatLogX.getPlugin();
        PluginDescriptionFile information = plugin.getDescription();
        return information.getVersion();
    }

    private @NotNull String getRemoteVersion() {
        ICombatLogX combatLogX = getCombatLogX();
        ConfigurablePlugin plugin = combatLogX.getPlugin();

        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        SpigotUpdateManager updateManager = corePlugin.getSpigotUpdateManager();
        String spigotVersion = updateManager.getSpigotVersion(plugin);
        if (spigotVersion == null) {
            return "Not Available";
        }

        return spigotVersion;
    }
}
