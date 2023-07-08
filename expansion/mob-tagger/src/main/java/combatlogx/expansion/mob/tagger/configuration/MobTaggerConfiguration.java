package combatlogx.expansion.mob.tagger.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.IConfigurable;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class MobTaggerConfiguration implements IConfigurable {
    private final Set<EntityType> mobTypeSet;
    private final Set<SpawnReason> spawnReasonSet;

    private boolean mobTypeSetInverted;
    private boolean spawnReasonSetInverted;
    private String bypassPermissionName;

    private transient Permission bypassPermission;

    public MobTaggerConfiguration() {
        this.mobTypeSet = EnumSet.noneOf(EntityType.class);
        this.mobTypeSetInverted = false;

        this.spawnReasonSet = EnumSet.noneOf(SpawnReason.class);
        this.spawnReasonSetInverted = false;

        this.bypassPermissionName = null;
        this.bypassPermission = null;
    }

    @Override
    public void load(ConfigurationSection config) {
        setMobTypeSetInverted(config.getBoolean("mob-list-inverted", false));
        setSpawnReasonSetInverted(config.getBoolean("spawn-reason-list-inverted", false));
        setBypassPermissionName(config.getString("bypass-permission"));

        List<String> mobTypeNameList = config.getStringList("mob-list");
        Set<EntityType> mobTypeSet = parseEnums(mobTypeNameList, EntityType.class);
        setMobTypes(mobTypeSet);

        List<String> spawnReasonNameList = config.getStringList("spawn-reason-list");
        Set<SpawnReason> spawnReasonSet = parseEnums(spawnReasonNameList, SpawnReason.class);
        setSpawnReasons(spawnReasonSet);
    }

    public @NotNull Set<EntityType> getMobTypes() {
        return Collections.unmodifiableSet(this.mobTypeSet);
    }

    public void setMobTypes(@NotNull Collection<EntityType> typeCollection) {
        this.mobTypeSet.clear();
        this.mobTypeSet.addAll(typeCollection);
    }

    public @NotNull Set<SpawnReason> getSpawnReasons() {
        return Collections.unmodifiableSet(this.spawnReasonSet);
    }

    public void setSpawnReasons(@NotNull Collection<SpawnReason> reasonCollection) {
        this.spawnReasonSet.clear();
        this.spawnReasonSet.addAll(reasonCollection);
    }

    public @Nullable String getBypassPermissionName() {
        return this.bypassPermissionName;
    }

    public void setBypassPermissionName(@Nullable String bypassPermissionName) {
        this.bypassPermissionName = bypassPermissionName;
        this.bypassPermission = null;
    }

    public @Nullable Permission getBypassPermission() {
        if (this.bypassPermission != null) {
            return this.bypassPermission;
        }

        String permissionName = getBypassPermissionName();
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        String permissionDescription = "CombatLogX bypass permission for mob combat.";
        this.bypassPermission = new Permission(permissionName, permissionDescription, PermissionDefault.FALSE);
        return this.bypassPermission;
    }

    public boolean isMobTypeSetInverted() {
        return mobTypeSetInverted;
    }

    public void setMobTypeSetInverted(boolean mobTypeSetInverted) {
        this.mobTypeSetInverted = mobTypeSetInverted;
    }

    public boolean isSpawnReasonSetInverted() {
        return spawnReasonSetInverted;
    }

    public void setSpawnReasonSetInverted(boolean spawnReasonSetInverted) {
        this.spawnReasonSetInverted = spawnReasonSetInverted;
    }

    public boolean shouldNotTag(EntityType mobType) {
        Set<EntityType> mobTypeSet = getMobTypes();
        boolean inverted = isMobTypeSetInverted();
        boolean contains = mobTypeSet.contains(mobType);
        return (inverted == contains);
    }

    public boolean shouldNotTag(SpawnReason reason) {
        Set<SpawnReason> reasonSet = getSpawnReasons();
        boolean inverted = isSpawnReasonSetInverted();
        boolean contains = reasonSet.contains(reason);
        return (inverted != contains);
    }
}
