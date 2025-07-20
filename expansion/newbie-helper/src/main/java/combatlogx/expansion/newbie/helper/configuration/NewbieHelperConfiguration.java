package combatlogx.expansion.newbie.helper.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class NewbieHelperConfiguration implements IConfigurable {
    private boolean newPlayerProtection;
    private boolean removeProtectionOnAttack;
    private long protectionTime;
    private boolean mobProtection;
    private boolean pvpToggleDefaultStatus;
    private int pvpToggleCooldown;
    private String permissionName;
    private boolean preventPvpToggleInDisabledWorlds;
    private boolean newPlayerCauseDamage;

    private transient Permission permission;

    public NewbieHelperConfiguration() {
        this.newPlayerProtection = true;
        this.removeProtectionOnAttack = true;
        this.protectionTime = 30_000L;
        this.mobProtection = false;
        this.pvpToggleDefaultStatus = true;
        this.pvpToggleCooldown = 0;
        this.permissionName = null;
        this.preventPvpToggleInDisabledWorlds = true;
        this.newPlayerCauseDamage = true;

        this.permission = null;
    }

    @Override
    public void load(@NotNull final ConfigurationSection config) {
        setNewPlayerProtection(config.getBoolean("new-player-protection", true));
        setRemoveProtectionOnAttack(config.getBoolean("remove-protection-on-attack", true));
        setProtectionTime(config.getLong("protection-time", 30_000L));
        setMobProtection(config.getBoolean("mob-protection", true));
        setPvpToggleDefaultStatus(config.getBoolean("pvp-toggle-default-status", true));
        setPvpToggleCooldown(config.getInt("pvp-toggle-cooldown", 0));
        setPermissionName(config.getString("pvp-toggle-cooldown-bypass-permission"));
        setPreventPvpToggleInDisabledWorlds(config.getBoolean("prevent-pvp-toggle-in-disabled-worlds", true));
        setNewPlayerCauseDamage(config.getBoolean("new-player-cause-damage", true));
    }

    public boolean isNewPlayerProtection() {
        return this.newPlayerProtection;
    }

    public void setNewPlayerProtection(final boolean newPlayerProtection) {
        this.newPlayerProtection = newPlayerProtection;
    }

    public boolean isRemoveProtectionOnAttack() {
        return this.removeProtectionOnAttack;
    }

    public void setRemoveProtectionOnAttack(final boolean removeProtectionOnAttack) {
        this.removeProtectionOnAttack = removeProtectionOnAttack;
    }

    public long getProtectionTime() {
        return this.protectionTime;
    }

    public void setProtectionTime(final long protectionTime) {
        this.protectionTime = protectionTime;
    }

    public boolean isMobProtection() {
        return this.mobProtection;
    }

    public void setMobProtection(final boolean mobProtection) {
        this.mobProtection = mobProtection;
    }

    public boolean getPvpToggleDefaultStatus() {
        return this.pvpToggleDefaultStatus;
    }

    public void setPvpToggleDefaultStatus(final boolean pvpToggleDefaultStatus) {
        this.pvpToggleDefaultStatus = pvpToggleDefaultStatus;
    }

    public int getPvpToggleCooldown() {
        return this.pvpToggleCooldown;
    }

    public void setPvpToggleCooldown(final int pvpToggleCooldown) {
        this.pvpToggleCooldown = pvpToggleCooldown;
    }

    public @Nullable String getPermissionName() {
        return this.permissionName;
    }

    public void setPermissionName(@Nullable final String permissionName) {
        this.permissionName = permissionName;
    }

    public @Nullable Permission getPermission() {
        if (this.permission != null) {
            return this.permission;
        }

        final String permissionName = getPermissionName();
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        final String description = "CombatLogX Newbie Helper permission to bypass the cooldown for '/toggle-pvp'.";
        this.permission = new Permission(permissionName, description, PermissionDefault.OP);
        return this.permission;
    }

    public boolean isPreventPvpToggleInDisabledWorlds() {
        return this.preventPvpToggleInDisabledWorlds;
    }

    public void setPreventPvpToggleInDisabledWorlds(final boolean preventPvpToggleInDisabledWorlds) {
        this.preventPvpToggleInDisabledWorlds = preventPvpToggleInDisabledWorlds;
    }

    public boolean isNewPlayerCauseDamage() {
        return this.newPlayerCauseDamage;
    }

    private void setNewPlayerCauseDamage(final boolean newPlayerCauseDamage) {
        this.newPlayerCauseDamage = newPlayerCauseDamage;
    }
}
