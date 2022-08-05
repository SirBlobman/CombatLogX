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

import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.requirement.Requirement;
import me.clip.placeholderapi.PlaceholderAPI;

public final class Reward {
    private final RewardExpansion expansion;
    private final int chance, maxChance;
    private final boolean randomCommand;
    private final List<String> commandList;

    private List<String> worldList, mobList;
    private List<Requirement> requirementList;
    private boolean mobWhiteList, worldWhiteList;

    public Reward(RewardExpansion expansion, int chance, int maxChance, boolean randomCommand,
                  List<String> commandList) {
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

    public RewardExpansion getExpansion() {
        return this.expansion;
    }

    public int getChance() {
        return this.chance;
    }

    public int getMaxChance() {
        return this.maxChance;
    }

    public boolean isRandomCommand() {
        return this.randomCommand;
    }

    public List<String> getCommandList() {
        return Collections.unmodifiableList(this.commandList);
    }

    public List<Requirement> getRequirementList() {
        return Collections.unmodifiableList(this.requirementList);
    }

    public List<String> getWorldNameList() {
        return Collections.unmodifiableList(this.worldList);
    }

    public boolean isWorldWhiteList() {
        return this.worldWhiteList;
    }

    public void setWorldWhiteList(boolean whitelist) {
        this.worldWhiteList = whitelist;
    }

    public List<String> getMobTypeList() {
        return Collections.unmodifiableList(this.mobList);
    }

    public boolean isMobWhiteList() {
        return this.mobWhiteList;
    }

    public void setMobWhiteList(boolean whitelist) {
        this.mobWhiteList = whitelist;
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

    public void tryActivate(Player player, LivingEntity enemy) {
        if (canActivate(player, enemy)) {
            executeCommands(player, enemy);
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

        List<String> worldNameList = getWorldNameList();
        boolean contains = (worldNameList.contains("*") || worldNameList.contains(worldName));
        boolean whitelist = isWorldWhiteList();
        return (whitelist == contains);
    }

    private boolean checkMobType(LivingEntity entity) {
        EntityType entityType = entity.getType();
        String entityTypeName = entityType.name();

        List<String> mobTypeList = getMobTypeList();
        boolean contains = (mobTypeList.contains("*") || mobTypeList.contains(entityTypeName));
        boolean whitelist = isMobWhiteList();
        return (whitelist == contains);
    }

    private boolean checkRequirements(Player player, LivingEntity enemy) {
        List<Requirement> requirementList = getRequirementList();
        for (Requirement requirement : requirementList) {
            LivingEntity toCheck = (requirement.isEnemy() ? enemy : player);
            if (!requirement.meetsRequirement(toCheck)) {
                return false;
            }
        }

        return true;
    }

    private boolean calculateChance() {
        int chance = getChance();
        int maxChance = getMaxChance();
        if (chance >= maxChance) {
            return true;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomValue = random.nextInt(1, maxChance + 1);
        return (randomValue <= chance);
    }

    private List<String> getCommandsToExecute() {
        List<String> commandList = getCommandList();
        if (isRandomCommand()) {
            int commandListSize = commandList.size();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int randomIndex = random.nextInt(commandListSize);

            String command = this.commandList.get(randomIndex);
            return Collections.singletonList(command);
        }

        return new ArrayList<>(commandList);
    }

    private void executeCommands(Player player, LivingEntity enemy) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(enemy, "enemy must not be null!");

        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String enemyType = enemy.getType().name();
        Replacer replacer = command -> command.replace("{player}", playerName)
                .replace("{enemy}", enemyName)
                .replace("{enemy_type}", enemyType);

        RewardExpansion expansion = getExpansion();
        boolean placeholderAPI = expansion.usePlaceholderAPI();

        List<String> commandList = getCommandsToExecute();
        for (String command : commandList) {
            String realCommand = replacer.replace(command);
            if (placeholderAPI) {
                realCommand = replacePlaceholderAPI(player, realCommand);
            }

            executeCommand(realCommand);
        }
    }

    private String replacePlaceholderAPI(Player player, String string) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    private String getEntityName(LivingEntity entity) {
        RewardExpansion expansion = getExpansion();
        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private void executeCommand(String command) {
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
