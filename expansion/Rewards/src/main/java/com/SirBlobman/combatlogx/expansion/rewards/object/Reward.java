package com.SirBlobman.combatlogx.expansion.rewards.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.EntityHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;

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
    public Reward(ICombatLogX plugin, int chance, int maxChance, boolean mobWhitelist, boolean worldWhitelist, boolean randomCommand, List<String> mobTypeList, List<String> worldNameList, List<String> commandList) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
        this.chance = chance;
        this.maxChance = maxChance;
        this.mobWhitelist = mobWhitelist;
        this.worldWhitelist = worldWhitelist;
        this.randomCommand = randomCommand;
        this.mobTypeList = mobTypeList;
        this.worldNameList = worldNameList;
        this.commandList = commandList;
    }
    
    protected List<String> getCommands() {
        return new ArrayList<>(this.commandList);
    }
    
    protected List<String> getRandomCommand() {
        List<String> commandList = getCommands();
        int commandListSize = commandList.size();
        
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        int randomValue = rng.nextInt(commandListSize);
        
        String command = commandList.get(randomValue);
        return Collections.singletonList(command);
    }
    
    public void tryActivate(Player player, Entity enemy) {
        if(canActivateReward(player, enemy)) executeCommands(player, enemy);
    }
    
    protected boolean canActivateReward(Player player, Entity enemy) {
        if(player == null || enemy == null) return false;
        
        World world = player.getWorld();
        return (calculateChance() && isWorldAllowed(world) && isMobAllowed(enemy));
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
        CommandSender console = Bukkit.getConsoleSender();
        List<String> commandList = (this.randomCommand ? getRandomCommand() : getCommands());
        for(String command : commandList) {
            String realCommand = replacePlaceholders(player, enemy, command);
            Bukkit.dispatchCommand(console, realCommand);
        }
    }

    private String getEnemyName(Entity enemy) {
        if(enemy == null) return this.plugin.getLanguageMessage("errors.unknown-entity-name");
        MultiVersionHandler<?> multiVersionHandler = this.plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        return entityHandler.getName(enemy);
    }
    
    private String replacePlaceholders(Player player, Entity enemy, String string) {
        String playerName = player.getName();
        String enemyName = getEnemyName(enemy);
        String enemyType = enemy.getType().name();
        return string.replace("{player}", playerName)
                .replace("{enemy-name}", enemyName)
                .replace("{enemy-type}", enemyType);
    }
}