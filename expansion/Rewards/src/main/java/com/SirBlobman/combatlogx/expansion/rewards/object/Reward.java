package com.SirBlobman.combatlogx.expansion.rewards.object;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Reward {
    private final ICombatLogX plugin;
    private final int chance, maxChance;
    private final boolean mobWhitelist, worldWhitelist, randomCommand;
    private final List<String> mobTypeList, worldNameList, commandList;
    public Reward(ICombatLogX plugin, int chance, int maxChance, boolean mobWhitelist, boolean worldWhitelist,
                  boolean randomCommand, List<String> mobTypeList, List<String> worldNameList, List<String> commandList) {
        this.plugin = plugin;
        this.chance = chance;
        this.maxChance = maxChance;
        this.mobWhitelist = mobWhitelist;
        this.worldWhitelist = worldWhitelist;
        this.randomCommand = randomCommand;
        this.mobTypeList = mobTypeList;
        this.worldNameList = worldNameList;
        this.commandList = commandList;
    }

    private boolean calculateChance() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomValue = random.nextInt(this.maxChance);
        return (randomValue <= this.chance);
    }

    private boolean isMobAllowed(Entity entity) {
        EntityType type = entity.getType();
        return isMobAllowed(type);
    }

    private boolean isMobAllowed(EntityType type) {
        String mobName = type.name();
        boolean contains = this.mobTypeList.contains(mobName);
        return (this.mobWhitelist == contains);
    }

    private boolean isWorldAllowed(World world) {
        String worldName = world.getName();
        boolean contains = this.worldNameList.contains(worldName);
        return (this.worldWhitelist == contains);
    }

    private void executeCommands(Player player, Entity enemy) {
        List<String> commandList = new ArrayList<>(this.commandList);
        if(this.randomCommand) {
            int commandListSize = commandList.size();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int randomValue = random.nextInt(commandListSize);
            String randomCommand = commandList.get(randomValue);
            commandList = Util.newList(randomCommand);
        }

        String playerName = player.getName();
        String enemyName = getEnemyName(enemy);
        String enemyType = enemy.getType().name();
        CommandSender console = Bukkit.getConsoleSender();
        for(String command : commandList) {
            command = command.replace("{player}", playerName).replace("{enemy-name}", enemyName).replace("{enemy-type}", enemyType);
            Bukkit.dispatchCommand(console, command);
        }
    }

    private boolean canActivateReward(Player player, Entity enemy) {
        if(player == null || enemy == null) return false;

        World world = player.getWorld();
        return (calculateChance() && isMobAllowed(enemy) && isWorldAllowed(world));
    }

    public void tryActivate(Player player, Entity enemy) {
        if(canActivateReward(player, enemy)) executeCommands(player, enemy);
    }

    private String getEnemyName(Entity enemy) {
        if(enemy == null) return this.plugin.getLanguageMessage("errors.unknown-entity-name");
        if(enemy instanceof Player) {
            Player player = (Player) enemy;
            return player.getName();
        }

        if(NMS_Handler.getMinorVersion() <= 7) {
            EntityType type = enemy.getType();
            return type.name();
        }

        return enemy.getName();
    }
}