package combatlogx.expansion.compatibility.citizens.configuration;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.object.DeathInventoryMode;

/**
 * Settings from 'citizens.yml'.
 * <p>
 * {@link #load(ConfigurationSection)} only overwrites the values whose keys are present in the section.
 * This makes it possible to load the global settings first and then apply a partial per-world
 * override section on top of a {@link #copy()} of it.
 */
public final class CitizensConfiguration implements IConfigurable {
    private final CitizensExpansion expansion;

    private boolean preventPunishments;
    private boolean preventLogin;
    private EntityType mobType;
    private boolean storeInventory;
    private DeathInventoryMode deathInventoryMode;
    private boolean storeLocation;
    private boolean mobTarget;
    private double mobTargetRadius;
    private int survivalTime;
    private boolean stayUntilEnemyEscapes;
    private boolean stayUntilNoDamage;
    private boolean preventResurrect;
    private boolean tagPlayer;
    private boolean alwaysSpawnNpcOnQuit;
    private String customNpcNameFormat;

    public CitizensConfiguration(@NotNull CitizensExpansion expansion) {
        this.expansion = expansion;
        resetToDefaults();
    }

    private @NotNull CitizensExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull Logger getLogger() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getLogger();
    }

    /**
     * Restore every option to the built-in default value.
     */
    public void resetToDefaults() {
        this.preventPunishments = true;
        this.preventLogin = false;
        this.mobType = EntityType.PLAYER;
        this.storeInventory = true;
        this.deathInventoryMode = DeathInventoryMode.DROP;
        this.storeLocation = true;
        this.mobTarget = true;
        this.mobTargetRadius = 10.0D;
        this.survivalTime = 30;
        this.stayUntilEnemyEscapes = false;
        this.stayUntilNoDamage = false;
        this.preventResurrect = true;
        this.tagPlayer = true;
        this.alwaysSpawnNpcOnQuit = false;
        this.customNpcNameFormat = "{player_name}";
    }

    /**
     * @return A new configuration with exactly the same values as this one.
     */
    public @NotNull CitizensConfiguration copy() {
        CitizensConfiguration copy = new CitizensConfiguration(getExpansion());
        copy.preventPunishments = this.preventPunishments;
        copy.preventLogin = this.preventLogin;
        copy.mobType = this.mobType;
        copy.storeInventory = this.storeInventory;
        copy.deathInventoryMode = this.deathInventoryMode;
        copy.storeLocation = this.storeLocation;
        copy.mobTarget = this.mobTarget;
        copy.mobTargetRadius = this.mobTargetRadius;
        copy.survivalTime = this.survivalTime;
        copy.stayUntilEnemyEscapes = this.stayUntilEnemyEscapes;
        copy.stayUntilNoDamage = this.stayUntilNoDamage;
        copy.preventResurrect = this.preventResurrect;
        copy.tagPlayer = this.tagPlayer;
        copy.alwaysSpawnNpcOnQuit = this.alwaysSpawnNpcOnQuit;
        copy.customNpcNameFormat = this.customNpcNameFormat;
        return copy;
    }

    /**
     * Load values from a section. Keys that are missing keep their current value.
     *
     * @param config The section to read from.
     */
    @Override
    public void load(ConfigurationSection config) {
        setPreventPunishments(config.getBoolean("prevent-punishments", isPreventPunishments()));
        setPreventLogin(config.getBoolean("prevent-login", isPreventLogin()));
        if (config.contains("mob-type")) {
            setMobType(config.getString("mob-type"));
        }

        setStoreInventory(config.getBoolean("store-inventory", isStoreInventory()));
        if (config.contains("death-inventory")) {
            setDeathInventoryMode(config.getString("death-inventory"));
        }

        setStoreLocation(config.getBoolean("store-location", isStoreLocation()));
        setMobTarget(config.getBoolean("mob-target", isMobTarget()));
        setMobTargetRadius(config.getDouble("mob-target-radius", getMobTargetRadius()));
        setSurvivalTime(config.getInt("survival-time", getSurvivalTime()));
        setStayUntilEnemyEscapes(config.getBoolean("stay-until-enemy-escapes", isStayUntilEnemyEscapes()));
        setStayUntilNoDamage(config.getBoolean("stay-until-no-damage", isStayUntilNoDamage()));
        setPreventResurrect(config.getBoolean("prevent-resurrect", isPreventResurrect()));
        setTagPlayer(config.getBoolean("tag-player", isTagPlayer()));
        setAlwaysSpawnNpcOnQuit(config.getBoolean("always-spawn-npc-on-quit", isAlwaysSpawnNpcOnQuit()));
        if (config.contains("custom-npc-name")) {
            setCustomNpcNameFormat(config.getString("custom-npc-name"));
        }
    }

    public boolean isPreventPunishments() {
        return preventPunishments;
    }

    public void setPreventPunishments(boolean preventPunishments) {
        this.preventPunishments = preventPunishments;
    }

    public boolean isPreventLogin() {
        return preventLogin;
    }

    public void setPreventLogin(boolean preventLogin) {
        this.preventLogin = preventLogin;
    }

    public @NotNull EntityType getMobType() {
        return mobType;
    }

    public void setMobType(@NotNull EntityType mobType) {
        this.mobType = mobType;
    }

    private void setMobType(@Nullable String mobTypeName) {
        if (mobTypeName == null) {
            mobTypeName = "PLAYER";
        }

        try {
            EntityType mobType = EntityType.valueOf(mobTypeName);
            if (!mobType.isAlive()) {
                Logger logger = getLogger();
                logger.warning("'" + mobType + "' is a non-living value, default to PLAYER.");
                setMobType(EntityType.PLAYER);
                return;
            }

            setMobType(mobType);
        } catch (IllegalArgumentException ex) {
            Logger logger = getLogger();
            logger.warning("'" + mobTypeName + "' is not a valid EntityType.");
            logger.warning("Defaulting to PLAYER.");
            setMobType(EntityType.PLAYER);
        }
    }

    public boolean isStoreInventory() {
        return storeInventory;
    }

    public void setStoreInventory(boolean storeInventory) {
        this.storeInventory = storeInventory;
    }

    public @NotNull DeathInventoryMode getDeathInventoryMode() {
        return deathInventoryMode;
    }

    public void setDeathInventoryMode(@NotNull DeathInventoryMode deathInventoryMode) {
        this.deathInventoryMode = deathInventoryMode;
    }

    private void setDeathInventoryMode(@Nullable String modeName) {
        DeathInventoryMode mode = DeathInventoryMode.parse(modeName);
        if (mode == null) {
            Logger logger = getLogger();
            logger.warning("'" + modeName + "' is not a valid death-inventory mode (DROP, KEEP, DESTROY).");
            logger.warning("Defaulting to DROP.");
            mode = DeathInventoryMode.DROP;
        }

        setDeathInventoryMode(mode);
    }

    public boolean isStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(boolean storeLocation) {
        this.storeLocation = storeLocation;
    }

    public boolean isMobTarget() {
        return mobTarget;
    }

    public void setMobTarget(boolean mobTarget) {
        this.mobTarget = mobTarget;
    }

    public double getMobTargetRadius() {
        return mobTargetRadius;
    }

    public void setMobTargetRadius(double mobTargetRadius) {
        this.mobTargetRadius = mobTargetRadius;
    }

    public int getSurvivalTime() {
        return survivalTime;
    }

    public void setSurvivalTime(int survivalTime) {
        this.survivalTime = survivalTime;
    }

    public boolean isStayUntilEnemyEscapes() {
        return stayUntilEnemyEscapes;
    }

    public void setStayUntilEnemyEscapes(boolean stayUntilEnemyEscapes) {
        this.stayUntilEnemyEscapes = stayUntilEnemyEscapes;
    }

    public boolean isStayUntilNoDamage() {
        return stayUntilNoDamage;
    }

    public void setStayUntilNoDamage(boolean stayUntilNoDamage) {
        this.stayUntilNoDamage = stayUntilNoDamage;
    }

    public boolean isPreventResurrect() {
        return preventResurrect;
    }

    public void setPreventResurrect(boolean preventResurrect) {
        this.preventResurrect = preventResurrect;
    }

    public boolean isTagPlayer() {
        return tagPlayer;
    }

    public void setTagPlayer(boolean tagPlayer) {
        this.tagPlayer = tagPlayer;
    }

    public boolean isAlwaysSpawnNpcOnQuit() {
        return alwaysSpawnNpcOnQuit;
    }

    public void setAlwaysSpawnNpcOnQuit(boolean alwaysSpawnNpcOnQuit) {
        this.alwaysSpawnNpcOnQuit = alwaysSpawnNpcOnQuit;
    }

    public @NotNull String getCustomNpcNameFormat() {
        return customNpcNameFormat;
    }

    public void setCustomNpcNameFormat(@Nullable String customNpcNameFormat) {
        Validate.notEmpty(customNpcNameFormat, "customNpcNameFormat must not be null or empty!");
        this.customNpcNameFormat = customNpcNameFormat;
    }
}
