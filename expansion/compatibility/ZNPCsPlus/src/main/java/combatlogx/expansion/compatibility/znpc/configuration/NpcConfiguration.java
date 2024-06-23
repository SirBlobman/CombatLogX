package combatlogx.expansion.compatibility.znpc.configuration;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.znpc.ZNPCExpansion;

public final class NpcConfiguration implements IConfigurable {
    private final ZNPCExpansion expansion;

    private boolean preventPunishments;
    private boolean preventLogin;
    private boolean storeInventory;
    private boolean storeLocation;
    private boolean mobTarget;
    private double mobTargetRadius;
    private int survivalTime;
    private boolean stayUntilEnemyEscape;
    private boolean stayUntilNoDamage;
    private boolean preventResurrect;
    private boolean tagPlayer;
    private boolean alwaysSpawnNpcOnQuit;
    private String customNpcNameFormat;

    private transient EntityType mobType;

    public NpcConfiguration(@NotNull ZNPCExpansion expansion) {
        this.expansion = expansion;

        this.preventPunishments = true;
        this.preventLogin = false;
        this.storeInventory = true;
        this.storeLocation = true;
        this.mobTarget = true;
        this.mobTargetRadius = 10.0D;
        this.survivalTime = 30;
        this.stayUntilEnemyEscape = false;
        this.stayUntilNoDamage = false;
        this.preventResurrect = true;
        this.tagPlayer = true;
        this.alwaysSpawnNpcOnQuit = false;
        this.customNpcNameFormat = "{player_name}";

        this.mobType = EntityType.PLAYER;
    }

    private @NotNull ZNPCExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull Logger getLogger() {
        return getExpansion().getLogger();
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setPreventPunishments(config.getBoolean("prevent-punishments", true));
        setPreventLogin(config.getBoolean("prevent-login", false));
        setMobType(config.getString("mob-type", "PLAYER"));
        setStoreInventory(config.getBoolean("store-inventory", true));
        setStoreLocation(config.getBoolean("store-location", true));
        setMobTarget(config.getBoolean("mob-target", true));
        setMobTargetRadius(config.getDouble("mob-target-radius", 10.0D));
        setSurvivalTime(config.getInt("survival-time", 30));
        setStayUntilEnemyEscape(config.getBoolean("stay-until-enemy-escapes", false));
        setStayUntilNoDamage(config.getBoolean("stay-until-no-damage", false));
        setPreventResurrect(config.getBoolean("prevent-resurrect", true));
        setTagPlayer(config.getBoolean("tag-player", true));
        setAlwaysSpawnNpcOnQuit(config.getBoolean("always-spawn-npc-on-quit", false));
        setCustomNpcNameFormat(config.getString("custom-npc-name", "{player_name}"));
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

        EntityType mobType;
        try {
            mobType = EntityType.valueOf(mobTypeName);

            if (!mobType.isAlive()) {
                Logger logger = getLogger();
                logger.warning("'" + mobType + "' is a non-living value, defaulting to PLAYER.");
                mobType = EntityType.PLAYER;
            }
        } catch (IllegalArgumentException ex) {
            Logger logger = getLogger();
            logger.warning("'" + mobTypeName + "' is not a valid EntityType.");
            logger.warning("Defaulting to PLAYER.");
            mobType = EntityType.PLAYER;
        }

        setMobType(mobType);
    }

    public boolean isStoreInventory() {
        return storeInventory;
    }

    public void setStoreInventory(boolean storeInventory) {
        this.storeInventory = storeInventory;
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

    public boolean isStayUntilEnemyEscape() {
        return stayUntilEnemyEscape;
    }

    public void setStayUntilEnemyEscape(boolean stayUntilEnemyEscape) {
        this.stayUntilEnemyEscape = stayUntilEnemyEscape;
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

    public void setCustomNpcNameFormat(String customNpcNameFormat) {
        Validate.notEmpty(customNpcNameFormat, "'custom-npc-name' must not be an empty string.");
        this.customNpcNameFormat = customNpcNameFormat;
    }
}
