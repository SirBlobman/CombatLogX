package combatlogx.expansion.rewards.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.requirement.Requirement;

public final class Reward {
    private final RewardExpansion expansion;
    private final int chance, maxChance;
    private final boolean randomCommand;
    private final List<String> commandList;

    private List<String> worldList, mobList;
    private List<Requirement> requirementList;
    private boolean mobWhiteList, worldWhiteList;

    public Reward(RewardExpansion expansion, int chance, int maxChance, boolean randomCommand, List<String> commandList) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        if (chance <= 0) {
            throw new IllegalArgumentException("chance must be greater than zero!");
        }

        if (maxChance <= 0) {
            throw new IllegalArgumentException("maxChance must be greater than zero!");
        }

        if (chance > maxChance) {
            throw new IllegalArgumentException("chance must be less than or equal to maxChance!");
        }

        this.chance = chance;
        this.maxChance = maxChance;
        this.randomCommand = randomCommand;

        if (commandList == null || commandList.isEmpty()) {
            throw new IllegalArgumentException("commandList cannot be empty or null!");
        }

        this.commandList = new ArrayList<>(commandList);
        this.requirementList = new ArrayList<>();

        this.worldList = new ArrayList<>();
        this.worldWhiteList = false;

        this.mobList = new ArrayList<>();
        this.mobWhiteList = false;
    }

    public void setRequirements(List<Requirement> requirementList) {
        if (requirementList == null) {
            throw new IllegalArgumentException("requirementList must not be null!");
        }

        this.requirementList = new ArrayList<>(requirementList);
    }

    public void setWorldList(List<String> worldList) {
        if (worldList == null) {
            throw new IllegalArgumentException("worldList must not be null!");
        }

        this.worldList = new ArrayList<>(worldList);
    }

    public void setMobList(List<String> mobList) {
        if (mobList == null) {
            throw new IllegalArgumentException("mobList must not be null!");
        }

        this.mobList = new ArrayList<>(mobList);
    }

    public void setWorldWhiteList(boolean whitelist) {
        this.worldWhiteList = whitelist;
    }

    public void setMobWhiteList(boolean whitelist) {
        this.mobWhiteList = whitelist;
    }

    public void tryActivate(Player player, LivingEntity enemy) {
        if (canActivate(player, enemy)) {
            runCommands(player, enemy);
        }
    }

    private boolean canActivate(Player player, LivingEntity enemy) {
        if (player == null || enemy == null) {
            return false;
        }

        boolean checkWorld = checkWorld(player);
        boolean checkMobType = checkMobType(enemy);
        boolean checkRequirements = checkRequirements(player, enemy);
        boolean checkChance = calculateChance();
        return (checkWorld && checkMobType && checkRequirements && checkChance);
    }

    private boolean checkWorld(Player player) {
        World world = player.getWorld();
        String worldName = world.getName();
        boolean contains = (this.worldList.contains("*") || this.worldList.contains(worldName));
        return (this.worldWhiteList == contains);
    }

    private boolean checkMobType(LivingEntity entity) {
        EntityType entityType = entity.getType();
        String entityTypeName = entityType.name();
        boolean contains = (this.mobList.contains("*") || this.mobList.contains(entityTypeName));
        return (this.mobWhiteList == contains);
    }

    private boolean checkRequirements(Player player, LivingEntity enemy) {
        for (Requirement requirement : this.requirementList) {
            LivingEntity toCheck = (requirement.isEnemy() ? enemy : player);
            if (requirement.meetsRequirement(toCheck)) {
                continue;
            }

            return false;
        }

        return true;
    }

    private boolean calculateChance() {
        if (this.chance >= this.maxChance) {
            return true;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomValue = random.nextInt(1, this.maxChance + 1);
        return (randomValue <= this.chance);
    }

    private List<String> getCommands() {
        if (this.randomCommand) {
            int commandListSize = this.commandList.size();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int randomIndex = random.nextInt(commandListSize);

            String command = this.commandList.get(randomIndex);
            return Collections.singletonList(command);
        }

        return new ArrayList<>(this.commandList);
    }

    private void runCommands(Player player, LivingEntity enemy) {
        if (player == null || enemy == null) {
            return;
        }

        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String enemyType = enemy.getType().name();

        List<String> commandList = getCommands();
        for (String command : commandList) {
            String realCommand = command.replace("{player}", playerName).replace("{enemy}", enemyName)
                    .replace("{enemy_type}", enemyType);

            if (this.expansion.usePlaceholderAPI()) {
                realCommand = replacePlaceholderAPI(player, realCommand);
            }

            if (this.expansion.useMVdWPlaceholderAPI()) {
                realCommand = replaceMVdWPlaceholderAPI(player, realCommand);
            }

            runCommand(realCommand);
        }
    }

    private String replacePlaceholderAPI(Player player, String string) {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
    }

    private String replaceMVdWPlaceholderAPI(Player player, String string) {
        return be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
    }

    private String getEntityName(LivingEntity entity) {
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private void runCommand(String command) {
        try {
            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        } catch (Exception ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "An error occurred while running the '/" + command
                    + "' command in console:", ex);
        }
    }
}
