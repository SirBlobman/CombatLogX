package combatlogx.expansion.rewards.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.configuration.Reward;
import combatlogx.expansion.rewards.configuration.RewardConfiguration;
import combatlogx.expansion.rewards.configuration.requirement.Requirement;
import me.clip.placeholderapi.PlaceholderAPI;

public final class ListenerRewards extends ExpansionListener {
    private final RewardExpansion expansion;

    public ListenerRewards(@NotNull RewardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }

        checkRewards(killer, entity);
    }

    private @NotNull RewardExpansion getRewardsExpansion() {
        return this.expansion;
    }

    private @NotNull RewardConfiguration getConfiguration() {
        RewardExpansion expansion = getRewardsExpansion();
        return expansion.getConfiguration();
    }

    private void checkRewards(@NotNull Player player, @NotNull LivingEntity enemy) {
        RewardConfiguration configuration = getConfiguration();
        Collection<Reward> rewards = configuration.getRewards();
        for (Reward reward : rewards) {
            checkReward(player, enemy, reward);
        }
    }

    private void checkReward(@NotNull Player player, @NotNull LivingEntity enemy, @NotNull Reward reward) {
        if (!hasPermission(player, reward)) {
            return;
        }

        if (isSelf(player, enemy)) {
            return;
        }

        World world = player.getWorld();
        if (!reward.contains(world)) {
            return;
        }

        EntityType mobType = enemy.getType();
        if (!reward.contains(mobType)) {
            return;
        }

        if (!checkChance(reward)) {
            return;
        }

        if (checkRequirements(enemy, reward)) {
            runCommands(player, enemy, reward);
        }
    }

    private boolean hasPermission(@NotNull Player player, @NotNull Reward reward) {
        Permission permission = reward.getPermission();
        if (permission != null) {
            return player.hasPermission(permission);
        }

        return true;
    }

    private boolean isSelf(@NotNull Player player, @NotNull LivingEntity enemy) {
        if (!(enemy instanceof Player)) {
            return false;
        }

        Player enemyPlayer = (Player) enemy;
        UUID playerId = player.getUniqueId();
        UUID enemyId = enemyPlayer.getUniqueId();
        return playerId.equals(enemyId);
    }

    private boolean checkChance(@NotNull Reward reward) {
        int chance = reward.getChance();
        int maxChance = reward.getMaxChance();
        if (chance >= maxChance) {
            return true;
        }

        Random random = new Random();
        int randomValue = random.nextInt(1, maxChance + 1);
        return (randomValue <= chance);
    }

    private boolean checkRequirements(@NotNull LivingEntity enemy, @NotNull Reward reward) {
        Collection<Requirement> requirements = reward.getRequirements();
        for (Requirement requirement : requirements) {
            if (!requirement.meetsRequirement(enemy)) {
                return false;
            }
        }

        return true;
    }

    private void runCommands(@NotNull Player player, @NotNull LivingEntity enemy, @NotNull Reward reward) {
        List<String> commandList = reward.getCommandList();
        if (commandList.isEmpty()) {
            return;
        }

        int commandListSize = commandList.size();
        if (reward.isRandomCommand() && commandListSize > 1) {
            Random random = new Random();
            int randomIndex = random.nextInt(commandListSize);
            commandList = Collections.singletonList(commandList.get(randomIndex));
        }

        List<String> replacedCommandList = new ArrayList<>();
        for (String command : commandList) {
            String replaced = replaceCommand(player, enemy, command);
            replacedCommandList.add(replaced);
        }

        runCommands(replacedCommandList);
    }

    private @NotNull String getEntityName(@NotNull Entity entity) {
        ICombatLogX plugin = getCombatLogX();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private @NotNull String replaceCommand(@NotNull Player player, @NotNull LivingEntity enemy,
                                           @NotNull String command) {
        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String enemyType = enemy.getType().name();
        String replaceBasic = command.replace("{player}", playerName)
                .replace("{enemy}", enemyName)
                .replace("{enemy_type}", enemyType);

        RewardConfiguration configuration = getConfiguration();
        if (configuration.isUsePlaceholderAPI()) {
            return PlaceholderAPI.setPlaceholders(player, replaceBasic);
        }

        return replaceBasic;
    }

    private void runCommands(@NotNull List<String> commandList) {
        CommandSender console = Bukkit.getConsoleSender();
        for (String command : commandList) {
            runCommand(console, command);
        }
    }

    private void runCommand(@NotNull CommandSender sender, @NotNull String command) {
        try {
            Bukkit.dispatchCommand(sender, command);
        } catch(Exception ex) {
            Logger logger = getExpansionLogger();
            String messageFormat = "Failed to execute command '/%s' in the console:";
            String logMessage = String.format(Locale.US, messageFormat, command);
            logger.log(Level.WARNING, logMessage, ex);
        }
    }
}
