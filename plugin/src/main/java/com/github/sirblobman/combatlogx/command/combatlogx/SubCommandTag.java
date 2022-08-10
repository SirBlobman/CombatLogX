package com.github.sirblobman.combatlogx.command.combatlogx;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class SubCommandTag extends CombatLogCommand {
    public SubCommandTag(ICombatLogX plugin) {
        super(plugin, "tag");
        setPermissionName("combatlogx.command.combatlogx.tag");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        if (args.length == 2) {
            IntStream intValueSet = IntStream.rangeClosed(1, 60);
            Set<String> valueSet = intValueSet.mapToObj(Integer::toString).collect(Collectors.toSet());
            return getMatching(args[1], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        Player target = findTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = target.getName();
        Replacer replacer = message -> message.replace("{target}", targetName);

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        boolean successfulTag;

        if (args.length < 2) {
            successfulTag = combatManager.tag(target, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        } else {
            BigInteger bigSeconds = parseInteger(sender, args[1]);
            if (bigSeconds == null) {
                return true;
            }

            long seconds = bigSeconds.longValue();
            long milliseconds = TimeUnit.SECONDS.toMillis(seconds);
            long systemMillis = System.currentTimeMillis();

            long combatEndTime = (systemMillis + milliseconds);
            successfulTag = combatManager.tag(target, null, TagType.UNKNOWN, TagReason.UNKNOWN, combatEndTime);
        }

        String messagePath = ("command.combatlogx." + (successfulTag ? "tag-player" : "tag-failure"));
        sendMessageWithPrefix(sender, messagePath, replacer);
        return true;
    }
}
