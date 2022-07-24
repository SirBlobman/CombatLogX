package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public final class SubCommandVersion extends CombatLogCommand {
    public SubCommandVersion(ICombatLogX plugin) {
        super(plugin, "version");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "combatlogx.command.combatlogx.version", true)) {
            return true;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This command only works in the server console.");
            return true;
        }

        List<String> messageList = new ArrayList<>();
        messageList.add("&f");

        addJavaVersionInformation(messageList);
        messageList.add("&f");
        addServerVersionInformation(messageList);
        messageList.add("&f");
        addDependencyInformation(messageList);
        messageList.add("&f");
        addPluginVersionInformation(messageList);
        messageList.add("&f");
        addExpansionInformation(messageList);
        messageList.add("&f");

        List<String> colorList = MessageUtility.colorList(messageList);
        for (String message : colorList) {
            sender.sendMessage(message);
        }

        return true;
    }

    private void addJavaVersionInformation(List<String> messageList) {
        try {
            String javaVersion = System.getProperty("java.version");
            String javaVendor = System.getProperty("java.vendor");
            messageList.add("&f&lJava Version: &7" + javaVersion);
            messageList.add("&f&lJava Vendor: &7" + javaVendor);
        } catch (SecurityException | IllegalArgumentException | NullPointerException ex) {
            messageList.add("&f&lJava Version: &7Unknown");
        }
    }

    private void addServerVersionInformation(List<String> messageList) {
        String version = Bukkit.getVersion();
        String bukkitVersion = Bukkit.getBukkitVersion();
        String minecraftVersion = VersionUtility.getMinecraftVersion();
        String nmsVersion = VersionUtility.getNetMinecraftServerVersion();

        messageList.add("&f&lServer Version: &7" + version);
        messageList.add("&f&lBukkit Version: &7" + bukkitVersion);
        messageList.add("&f&lMinecraft Version: &7" + minecraftVersion);
        messageList.add("&f&lNMS Version: &7" + nmsVersion);
    }

    private void addDependencyInformation(List<String> messageList) {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();
        PluginDescriptionFile information = plugin.getDescription();
        messageList.add("&f&lDependency Information:");

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
            messageList.add("&f&l - &7" + dependencyFullName);
        }
    }

    private void addPluginVersionInformation(List<String> messageList) {
        String pluginVersion = getPluginVersion();
        String spigotVersion = getSpigotVersion();

        messageList.add("&f&lCombatLogX by SirBlobman");
        messageList.add("&f&lPlugin Version: &7" + pluginVersion);
        messageList.add("&f&lSpigot Version: &7" + spigotVersion);
    }

    private void addExpansionInformation(List<String> messageList) {
        ExpansionManager expansionManager = getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();
        int enabledExpansionListSize = enabledExpansionList.size();

        messageList.add("&f&lEnabled Expansions (&7" + enabledExpansionListSize + "&f&l):");
        for (Expansion expansion : enabledExpansionList) {
            String expansionName = expansion.getName();
            messageList.add("&f&l - &7" + expansionName);
        }
    }

    private String getPluginVersion() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();
        PluginDescriptionFile information = plugin.getDescription();
        return information.getVersion();
    }

    private String getSpigotVersion() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();

        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        String spigotVersion = updateManager.getSpigotVersion(plugin);
        if (spigotVersion == null) {
            return "Not Available";
        }

        return spigotVersion;
    }
}
