package com.github.sirblobman.combatlogx.api.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.object.TimerType;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnum;
import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class MainConfiguration implements IConfigurable {
    private final ICombatLogX plugin;

    private String generatedByVersion;
    private boolean debugMode;

    private boolean broadcastOnLoad;
    private boolean broadcastOnEnable;
    private boolean broadcastOnDisable;

    private final Set<String> worldSet;
    private boolean worldListInverted;

    private boolean linkPets;
    private boolean linkProjectiles;
    private boolean linkFishingRod;
    private boolean linkTnt;
    private boolean linkEndCrystals;
    private final Set<EntityType> ignoredProjectileSet;

    private TimerType timerType;
    private int defaultTimer;

    private String bypassPermissionName;
    private transient Permission bypassPermission;

    private boolean selfCombat;
    private boolean untagOnSelfDeath;
    private boolean untagOnEnemyDeath;
    private boolean removeNoDamageCooldown;

    private int forgiveRequestCooldown;
    private int forgiveRequestExpire;

    private double minimumTps;

    public MainConfiguration(@NotNull ICombatLogX plugin) {
        this.plugin = plugin;
        this.generatedByVersion = plugin.getPlugin().getDescription().getVersion();
        this.debugMode = false;

        this.broadcastOnLoad = true;
        this.broadcastOnEnable = true;
        this.broadcastOnDisable = true;

        this.worldSet = new HashSet<>();
        this.worldListInverted = false;

        this.linkPets = true;
        this.linkProjectiles = true;
        this.linkFishingRod =true;
        this.linkTnt = true;
        this.linkEndCrystals = true;
        this.ignoredProjectileSet = EnumSet.noneOf(EntityType.class);

        this.timerType = TimerType.GLOBAL;
        this.defaultTimer = 10;

        this.bypassPermissionName = null;
        this.bypassPermission = null;

        this.selfCombat = false;
        this.untagOnSelfDeath = true;
        this.untagOnEnemyDeath = true;
        this.removeNoDamageCooldown = true;

        this.forgiveRequestCooldown = 30;
        this.forgiveRequestExpire = 10;

        this.minimumTps = 15.0D;
    }

    @Override
    public void load(ConfigurationSection config) {
        String pluginVersion = this.plugin.getPlugin().getDescription().getVersion();
        setGeneratedByVersion(config.getString("generated-by-version", pluginVersion));
        setDebugMode(config.getBoolean("debug-mode", false));

        ConfigurationSection broadcastSection = getOrCreateSection(config, "broadcast");
        setBroadcastOnLoad(broadcastSection.getBoolean("on-load", true));
        setBroadcastOnEnable(broadcastSection.getBoolean("on-enable", true));
        setBroadcastOnDisable(broadcastSection.getBoolean("on-disable", true));

        List<String> worldNameList = config.getStringList("disabled-world-list");
        setWorlds(worldNameList);
        setWorldListInverted(config.getBoolean("disabled-world-list-inverted"));

        setLinkPets(config.getBoolean("link-pets", true));
        setLinkProjectiles(config.getBoolean("link-projectiles", true));
        setLinkFishingRod(config.getBoolean("link-fishing-rod", true));
        setLinkTnt(config.getBoolean("link-tnt", true));
        setLinkEndCrystals(config.getBoolean("link-end-crystal", true));

        List<String> projectileNameList = config.getStringList("ignored-projectiles");
        Set<EntityType> ignoredProjectileSet = parseEnums(projectileNameList, EntityType.class);
        setIgnoredProjectiles(ignoredProjectileSet);

        ConfigurationSection timerSection = getOrCreateSection(config, "timer");
        String timerTypeName = timerSection.getString("type", "GLOBAL");
        setTimerType(parseEnum(TimerType.class, timerTypeName, TimerType.GLOBAL));
        setDefaultTimer(timerSection.getInt("default-timer", 10));

        setBypassPermissionName(config.getString("bypass-permission"));
        setSelfCombat(config.getBoolean("self-combat", false));
        setUntagOnSelfDeath(config.getBoolean("untag-on-death", true));
        setUntagOnEnemyDeath(config.getBoolean("untag-on-enemy-death", true));
        setRemoveNoDamageCooldown(config.getBoolean("remove-no-damage-cooldown", true));

        setForgiveRequestCooldown(config.getInt("forgive-request-cooldown", 30));
        setForgiveRequestExpire(config.getInt("forgive-request-expire", 10));
        setMinimumTps(config.getDouble("minimum-tps", 15.0D));
    }

    public @NotNull String getGeneratedByVersion() {
        return this.generatedByVersion;
    }

    public void setGeneratedByVersion(@NotNull String generatedByVersion) {
        this.generatedByVersion = generatedByVersion;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isBroadcastOnLoad() {
        return this.broadcastOnLoad;
    }

    public void setBroadcastOnLoad(boolean broadcastOnLoad) {
        this.broadcastOnLoad = broadcastOnLoad;
    }

    public boolean isBroadcastOnEnable() {
        return this.broadcastOnEnable;
    }

    public void setBroadcastOnEnable(boolean broadcastOnEnable) {
        this.broadcastOnEnable = broadcastOnEnable;
    }

    public boolean isBroadcastOnDisable() {
        return this.broadcastOnDisable;
    }

    public void setBroadcastOnDisable(boolean broadcastOnDisable) {
        this.broadcastOnDisable = broadcastOnDisable;
    }

    public @NotNull Set<String> getWorlds() {
        return Collections.unmodifiableSet(this.worldSet);
    }

    public void setWorlds(@NotNull Collection<String> worlds) {
        this.worldSet.clear();
        this.worldSet.addAll(worlds);
    }

    public boolean isWorldListInverted() {
        return this.worldListInverted;
    }

    public void setWorldListInverted(boolean worldListInverted) {
        this.worldListInverted = worldListInverted;
    }

    public boolean isLinkPets() {
        return this.linkPets;
    }

    public void setLinkPets(boolean linkPets) {
        this.linkPets = linkPets;
    }

    public boolean isLinkProjectiles() {
        return this.linkProjectiles;
    }

    public void setLinkProjectiles(boolean linkProjectiles) {
        this.linkProjectiles = linkProjectiles;
    }

    public boolean isLinkFishingRod() {
        return this.linkFishingRod;
    }

    public void setLinkFishingRod(boolean linkFishingRod) {
        this.linkFishingRod = linkFishingRod;
    }

    public boolean isLinkTnt() {
        return this.linkTnt;
    }

    public void setLinkTnt(boolean linkTnt) {
        this.linkTnt = linkTnt;
    }

    public boolean isLinkEndCrystals() {
        return this.linkEndCrystals;
    }

    public void setLinkEndCrystals(boolean linkEndCrystals) {
        this.linkEndCrystals = linkEndCrystals;
    }

    public @NotNull Set<EntityType> getIgnoredProjectiles() {
        return Collections.unmodifiableSet(this.ignoredProjectileSet);
    }

    public void setIgnoredProjectiles(@NotNull Collection<EntityType> ignoredProjectiles) {
        this.ignoredProjectileSet.clear();
        this.ignoredProjectileSet.addAll(ignoredProjectiles);
    }

    public @NotNull TimerType getTimerType() {
        return this.timerType;
    }

    public void setTimerType(@NotNull TimerType timerType) {
        this.timerType = timerType;
    }

    public int getDefaultTimer() {
        return defaultTimer;
    }

    public void setDefaultTimer(int defaultTimer) {
        this.defaultTimer = defaultTimer;
    }

    public @Nullable String getBypassPermissionName() {
        return this.bypassPermissionName;
    }

    public void setBypassPermissionName(@Nullable String bypassPermissionName) {
        this.bypassPermissionName = bypassPermissionName;
        this.bypassPermission = null;
    }

    public Permission getBypassPermission() {
        if (this.bypassPermission != null) {
            return this.bypassPermission;
        }

        String permissionName = getBypassPermissionName();
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        String description = "CombatLogX bypass tagging permission.";
        this.bypassPermission = new Permission(permissionName, description, PermissionDefault.FALSE);
        return this.bypassPermission;
    }

    public boolean isSelfCombat() {
        return this.selfCombat;
    }

    public void setSelfCombat(boolean selfCombat) {
        this.selfCombat = selfCombat;
    }

    public boolean isUntagOnSelfDeath() {
        return this.untagOnSelfDeath;
    }

    public void setUntagOnSelfDeath(boolean untagOnSelfDeath) {
        this.untagOnSelfDeath = untagOnSelfDeath;
    }

    public boolean isUntagOnEnemyDeath() {
        return this.untagOnEnemyDeath;
    }

    public void setUntagOnEnemyDeath(boolean untagOnEnemyDeath) {
        this.untagOnEnemyDeath = untagOnEnemyDeath;
    }

    public boolean isRemoveNoDamageCooldown() {
        return this.removeNoDamageCooldown;
    }

    public void setRemoveNoDamageCooldown(boolean removeNoDamageCooldown) {
        this.removeNoDamageCooldown = removeNoDamageCooldown;
    }

    public int getForgiveRequestCooldown() {
        return this.forgiveRequestCooldown;
    }

    public void setForgiveRequestCooldown(int forgiveRequestCooldown) {
        this.forgiveRequestCooldown = forgiveRequestCooldown;
    }

    public int getForgiveRequestExpire() {
        return this.forgiveRequestExpire;
    }

    public void setForgiveRequestExpire(int forgiveRequestExpire) {
        this.forgiveRequestExpire = forgiveRequestExpire;
    }

    public double getMinimumTps() {
        return this.minimumTps;
    }

    public void setMinimumTps(double minimumTps) {
        this.minimumTps = minimumTps;
    }

    public boolean isDisabled(@NotNull World world) {
        Set<String> worldNameSet = getWorlds();
        boolean inverted = isWorldListInverted();

        String worldName = world.getName();
        boolean contains = worldNameSet.contains(worldName);
        return (inverted != contains);
    }

    public boolean isProjectileIgnored(@NotNull EntityType type) {
        Set<EntityType> ignoredProjectileSet = getIgnoredProjectiles();
        return ignoredProjectileSet.contains(type);
    }
}
