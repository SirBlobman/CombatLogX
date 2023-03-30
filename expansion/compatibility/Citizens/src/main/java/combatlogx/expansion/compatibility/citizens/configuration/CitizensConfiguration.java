package combatlogx.expansion.compatibility.citizens.configuration;

import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.utility.Validate;

public final class CitizensConfiguration implements IConfigurable {
    private final Logger logger;

    private boolean preventPunishments;
    private boolean preventLogin;
    private EntityType mobType;
    private boolean storeInventory;
    private boolean storeLocation;
    private boolean mobTarget;
    private double mobTargetRadius;
    private int survivalTime;
    private boolean stayUntilEnemyEscapes;
    private boolean stayUntilNoDamage;
    private boolean preventResurrect;
    private boolean tagPlayer;
    private boolean alwaysSpawnNpcOnQuit;

    public CitizensConfiguration(Logger logger) {
        this.logger = Validate.notNull(logger, "logger must not be null!");

        this.preventPunishments = true;
        this.preventLogin = false;
        this.mobType = EntityType.PLAYER;
        this.storeInventory = true;
        this.storeLocation = true;
        this.mobTarget = true;
        this.mobTargetRadius = 10.0D;
        this.survivalTime = 30;
        this.stayUntilEnemyEscapes = false;
        this.stayUntilNoDamage = false;
        this.preventResurrect = true;
        this.tagPlayer = true;
        this.alwaysSpawnNpcOnQuit = false;
    }

    private Logger getLogger() {
        return this.logger;
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventPunishments(config.getBoolean("prevent-punishments", true));
        setPreventLogin(config.getBoolean("prevent-login", false));
        setMobType(config.getString("mob-type", "PLAYER"));
        setStoreInventory(config.getBoolean("store-inventory", true));
        setStoreLocation(config.getBoolean("store-location", true));
        setMobTarget(config.getBoolean("mob-target", true));
        setMobTargetRadius(config.getDouble("mob-target-radius", 10.0D));
        setSurvivalTime(config.getInt("survival-time", 30));
        setStayUntilEnemyEscapes(config.getBoolean("stay-until-enemy-escapes", false));
        setStayUntilNoDamage(config.getBoolean("stay-until-no-damage", false));
        setPreventResurrect(config.getBoolean("prevent-resurrect", true));
        setTagPlayer(config.getBoolean("tag-player", true));
        setAlwaysSpawnNpcOnQuit(config.getBoolean("always-spawn-npc-on-quit", false));
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

    public EntityType getMobType() {
        return mobType;
    }

    public void setMobType(EntityType mobType) {
        this.mobType = mobType;
    }

    private void setMobType(String mobTypeName) {
        Logger logger = getLogger();
        if (mobTypeName == null) {
            logger.warning("mob-type is null, defaulting to PLAYER.");
            setMobType(EntityType.PLAYER);
            return;
        }


        try {
            EntityType mobType = EntityType.valueOf(mobTypeName);
            if (!mobType.isAlive()) {
                logger.warning("'" + mobType + "' is a non-living value, default to PLAYER.");
                setMobType(EntityType.PLAYER);
                return;
            }

            setMobType(mobType);
        } catch(IllegalArgumentException ex) {
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
}
