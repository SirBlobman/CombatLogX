package combatlogx.expansion.compatibility.placeholderapi;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public final class HookPlaceholderAPI extends PlaceholderExpansion {
    private final PlaceholderAPIExpansion expansion;

    public HookPlaceholderAPI(@NotNull PlaceholderAPIExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        PluginDescriptionFile descriptionFile = getDescriptionFile();
        String pluginName = descriptionFile.getName();
        return pluginName.toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        PluginDescriptionFile descriptionFile = getDescriptionFile();
        List<String> authorList = descriptionFile.getAuthors();
        return String.join(", ", authorList);
    }

    @Override
    public @NotNull String getVersion() {
        PlaceholderAPIExpansion expansion = getExpansion();
        ExpansionDescription description = expansion.getDescription();
        return description.getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String placeholder) {
        if (player == null) {
            return null;
        }

        ICombatLogX plugin = getCombatLogX();
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        if (!placeholder.startsWith("newbie_helper_")) {
            placeholder = ("combatlogx_" + placeholder);
        }

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        List<Entity> enemyList = (tagInformation == null ? Collections.emptyList() : tagInformation.getEnemies());
        return placeholderManager.getPlaceholderReplacement(player, enemyList, placeholder);
    }

    private @NotNull PlaceholderAPIExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull ICombatLogX getCombatLogX() {
        PlaceholderAPIExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull PluginDescriptionFile getDescriptionFile() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();
        return plugin.getDescription();
    }
}
