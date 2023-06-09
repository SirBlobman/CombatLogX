package com.github.sirblobman.combatlogx.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;
import com.github.sirblobman.combatlogx.api.placeholder.PlaceholderHelper;
import com.github.sirblobman.combatlogx.api.utility.CommandHelper;
import com.github.sirblobman.api.shaded.adventure.text.Component;

public final class PlaceholderManager extends Manager implements IPlaceholderManager {
    private static final Pattern BRACKET_PLACEHOLDER_PATTERN;

    static {
        BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\S+)}");
    }

    private final Map<String, IPlaceholderExpansion> expansionMap;

    public PlaceholderManager(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.expansionMap = new LinkedHashMap<>();
    }

    @Override
    public @Nullable IPlaceholderExpansion getPlaceholderExpansion(@NotNull String id) {
        return this.expansionMap.get(id);
    }

    @Override
    public @NotNull List<IPlaceholderExpansion> getPlaceholderExpansions() {
        Collection<IPlaceholderExpansion> valueCollection = this.expansionMap.values();
        List<IPlaceholderExpansion> valueList = new ArrayList<>(valueCollection);
        return Collections.unmodifiableList(valueList);
    }

    @Override
    public void registerPlaceholderExpansion(@NotNull IPlaceholderExpansion expansion) {
        String expansionId = expansion.getId();
        IPlaceholderExpansion oldRegistry = this.expansionMap.putIfAbsent(expansionId, expansion);
        if (oldRegistry != null) {
            String errorMessage = "A placeholder expansion with id '" + expansionId + "' is already registered.";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public @Nullable String getPlaceholderReplacement(@NotNull Player player, @NotNull List<Entity> enemyList,
                                                      @NotNull String placeholder) {
        int underscoreIndex = placeholder.indexOf('_');
        if (underscoreIndex == -1) {
            return null;
        }

        String expansionId = placeholder.substring(0, underscoreIndex);
        IPlaceholderExpansion expansion = getPlaceholderExpansion(expansionId);
        if (expansion == null) {
            return null;
        }

        String subPlaceholder = placeholder.substring(underscoreIndex + 1);
        return expansion.getReplacementString(player, enemyList, subPlaceholder);
    }

    @Override
    public @Nullable Component getPlaceholderReplacementComponent(@NotNull Player player,
                                                                  @NotNull List<Entity> enemyList,
                                                                  @NotNull String placeholder) {
        int underscoreIndex = placeholder.indexOf('_');
        if (underscoreIndex == -1) {
            return null;
        }

        String expansionId = placeholder.substring(0, underscoreIndex);
        IPlaceholderExpansion expansion = getPlaceholderExpansion(expansionId);
        if (expansion == null) {
            return null;
        }

        String subPlaceholder = placeholder.substring(underscoreIndex + 1);
        return expansion.getReplacement(player, enemyList, subPlaceholder);
    }

    @Override
    public @NotNull String replaceAll(@NotNull Player player, @NotNull List<Entity> enemyList,
                                      @NotNull String string) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = BRACKET_PLACEHOLDER_PATTERN.matcher(string);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = getPlaceholderReplacement(player, enemyList, placeholder);
            if (replacement != null) {
                matcher.appendReplacement(buffer, replacement);
            }
        }

        matcher.appendTail(buffer);
        String replaced = buffer.toString();
        return PlaceholderHelper.replacePlaceholderAPI(player, replaced);
    }

    @Override
    public void runReplacedCommands(@NotNull Player player, @NotNull List<Entity> enemyList,
                                    @NotNull Iterable<String> commands) {
        ICombatLogX plugin = getCombatLogX();
        for (String originalCommand : commands) {
            String replacedCommand = replaceAll(player, enemyList, originalCommand);
            if (replacedCommand.startsWith("[PLAYER]")) {
                String playerCommand = replacedCommand.substring(8);
                CommandHelper.runSync(plugin, () -> CommandHelper.runAsPlayer(plugin, player, playerCommand));
            } else if (replacedCommand.startsWith("[OP]")) {
                String opCommand = replacedCommand.substring(4);
                CommandHelper.runSync(plugin, () -> CommandHelper.runAsOperator(plugin, player, opCommand));
            } else {
                CommandHelper.runSync(plugin, () -> CommandHelper.runAsConsole(plugin, replacedCommand));
            }
        }
    }
}
