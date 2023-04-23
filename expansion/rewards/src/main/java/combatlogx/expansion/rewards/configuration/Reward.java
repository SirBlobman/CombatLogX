package combatlogx.expansion.rewards.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.IConfigurable;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.configuration.requirement.EconomyRequirement;
import combatlogx.expansion.rewards.configuration.requirement.ExperienceRequirement;
import combatlogx.expansion.rewards.configuration.requirement.Requirement;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class Reward implements IConfigurable {
    private final RewardExpansion expansion;
    private final String id;
    private final Set<EntityType> mobTypeList;
    private final Set<String> worldList;
    private final List<String> commandList;
    private String permissionName;
    private int chance;
    private int maxChance;
    private boolean mobWhiteList;
    private boolean worldWhiteList;
    private boolean randomCommand;
    private Map<String, Requirement> requirementMap;

    private transient Permission permission;

    public Reward(@NotNull RewardExpansion expansion, @NotNull String id) {
        this.expansion = expansion;
        this.id = id;

        this.permissionName = null;
        this.chance = 1;
        this.maxChance = 2;
        this.mobWhiteList = true;
        this.mobTypeList = EnumSet.noneOf(EntityType.class);
        this.worldWhiteList = true;
        this.worldList = new HashSet<>();
        this.randomCommand = false;
        this.commandList = new ArrayList<>();
        this.requirementMap = new HashMap<>();

        this.permission = null;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setPermissionName(section.getString("permission"));
        setChance(section.getInt("chance", 5));
        setMaxChance(section.getInt("max-chance", 1_000));

        setWorldWhiteList(section.getBoolean("world-whitelist", true));
        setWorldList(section.getStringList("world-list"));

        setMobWhiteList(section.getBoolean("mob-whitelist", true));
        List<String> mobTypeNameList = section.getStringList("mob-list");
        setMobTypeList(parseEnums(mobTypeNameList, EntityType.class));

        setRandomCommand(section.getBoolean("random-command", false));
        setCommandList(section.getStringList("commands"));

        resetRequirements();
        loadRequirements(getOrCreateSection(section, "requirements"));
    }

    private void loadRequirements(ConfigurationSection section) {
        Set<String> requirementIdSet = section.getKeys(false);
        for (String requirementId : requirementIdSet) {
            ConfigurationSection sectionRequirement = section.getConfigurationSection(requirementId);
            if (sectionRequirement == null) {
                continue;
            }

            String type = sectionRequirement.getString("type");
            if (type == null) {
                continue;
            }

            Requirement requirement;
            switch (type) {
                case "economy":
                    requirement = loadEconomyRequirement(requirementId, sectionRequirement);
                    break;
                case "experience":
                    requirement = loadExperienceRequirement(requirementId, sectionRequirement);
                    break;
                default:
                    requirement = null;
                    break;
            }

            if (requirement != null) {
                addRequirement(requirement);
            }
        }
    }

    private @NotNull EconomyRequirement loadEconomyRequirement(@NotNull String id,
                                                               @NotNull ConfigurationSection section) {
        RewardExpansion expansion = getExpansion();
        EconomyRequirement requirement = new EconomyRequirement(expansion, id);
        requirement.load(section);
        return requirement;
    }

    private @NotNull ExperienceRequirement loadExperienceRequirement(@NotNull String id,
                                                                     @NotNull ConfigurationSection section) {
        RewardExpansion expansion = getExpansion();
        ExperienceRequirement requirement = new ExperienceRequirement(expansion, id);
        requirement.load(section);
        return requirement;
    }

    private @NotNull RewardExpansion getExpansion() {
        return this.expansion;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @Nullable String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(@Nullable String permissionName) {
        this.permissionName = permissionName;
        this.permission = null;
    }

    public @Nullable Permission getPermission() {
        if (this.permission != null) {
            return this.permission;
        }

        String permissionName = getPermissionName();
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        String description = "CombatLogX Rewards access permission.";
        this.permission = new Permission(permissionName, description, PermissionDefault.FALSE);
        return this.permission;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public int getMaxChance() {
        return maxChance;
    }

    public void setMaxChance(int maxChance) {
        this.maxChance = maxChance;
    }

    public boolean isMobWhiteList() {
        return mobWhiteList;
    }

    public void setMobWhiteList(boolean mobWhiteList) {
        this.mobWhiteList = mobWhiteList;
    }

    public Set<EntityType> getMobTypeList() {
        return mobTypeList;
    }

    public void setMobTypeList(@NotNull Collection<EntityType> mobTypeList) {
        this.mobTypeList.clear();
        this.mobTypeList.addAll(mobTypeList);
    }

    public boolean isWorldWhiteList() {
        return worldWhiteList;
    }

    public void setWorldWhiteList(boolean worldWhiteList) {
        this.worldWhiteList = worldWhiteList;
    }

    public Set<String> getWorldList() {
        return Collections.unmodifiableSet(this.worldList);
    }

    public void setWorldList(@NotNull Collection<String> worldList) {
        this.worldList.clear();
        this.worldList.addAll(worldList);
    }

    public boolean isRandomCommand() {
        return randomCommand;
    }

    public void setRandomCommand(boolean randomCommand) {
        this.randomCommand = randomCommand;
    }

    public @NotNull List<String> getCommandList() {
        return Collections.unmodifiableList(this.commandList);
    }

    public void setCommandList(@NotNull Collection<String> commandList) {
        this.commandList.clear();
        this.commandList.addAll(commandList);
    }

    public @NotNull Map<String, Requirement> getRequirementMap() {
        return Collections.unmodifiableMap(this.requirementMap);
    }

    public @NotNull Collection<Requirement> getRequirements() {
        Map<String, Requirement> requirementMap = getRequirementMap();
        Collection<Requirement> requirements = requirementMap.values();
        return Collections.unmodifiableCollection(requirements);
    }

    public void addRequirement(Requirement requirement) {
        String id = requirement.getId();
        this.requirementMap.put(id, requirement);
    }

    public void resetRequirements() {
        this.requirementMap.clear();
    }

    public boolean contains(@NotNull World world) {
        Set<String> worldList = getWorldList();
        boolean whitelist = isWorldWhiteList();

        String worldName = world.getName();
        boolean contains = worldList.contains(worldName);
        return (whitelist == contains);
    }

    public boolean contains(@NotNull EntityType mobType) {
        Set<EntityType> mobTypeList = getMobTypeList();
        boolean whitelist = isMobWhiteList();
        boolean contains = mobTypeList.contains(mobType);
        return (whitelist == contains);
    }
}
