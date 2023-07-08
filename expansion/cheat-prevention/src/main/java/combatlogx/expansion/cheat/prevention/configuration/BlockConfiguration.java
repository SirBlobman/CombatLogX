package combatlogx.expansion.cheat.prevention.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.shaded.xseries.XMaterial;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class BlockConfiguration implements IBlockConfiguration {
    private final Set<XMaterial> preventInteractionSet;
    private final Set<XMaterial> preventBreakingSet;
    private final Set<XMaterial> preventPlacingSet;
    private boolean preventInteraction;
    private boolean preventBreaking;
    private boolean preventPlacing;
    private boolean preventPortalCreation;

    public BlockConfiguration() {
        this.preventInteraction = false;
        this.preventBreaking = true;
        this.preventPlacing = true;
        this.preventPortalCreation = false;

        this.preventInteractionSet = EnumSet.noneOf(XMaterial.class);
        this.preventBreakingSet = EnumSet.noneOf(XMaterial.class);
        this.preventPlacingSet = EnumSet.noneOf(XMaterial.class);
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventInteraction(config.getBoolean("prevent-interaction", false));
        setPreventBreaking(config.getBoolean("prevent-breaking", true));
        setPreventPlacing(config.getBoolean("prevent-placing", true));
        setPreventPortalCreation(config.getBoolean("prevent-portal-creation", false));

        setPreventInteractionTypes(parseEnums(config.getStringList("prevent-interaction-list"), XMaterial.class));
        setPreventBreakingTypes(parseEnums(config.getStringList("prevent-breaking-list"), XMaterial.class));
        setPreventPlacingTypes(parseEnums(config.getStringList("prevent-placing-list"), XMaterial.class));
    }

    @Override
    public boolean isPreventInteraction() {
        return this.preventInteraction;
    }

    public void setPreventInteraction(boolean value) {
        this.preventInteraction = value;
    }

    @Override
    public boolean isPreventBreaking() {
        return this.preventBreaking;
    }

    public void setPreventBreaking(boolean value) {
        this.preventBreaking = value;
    }

    @Override
    public boolean isPreventPlacing() {
        return this.preventPlacing;
    }

    public void setPreventPlacing(boolean value) {
        this.preventPlacing = value;
    }

    @Override
    public boolean isPreventPortalCreation() {
        return this.preventPortalCreation;
    }

    public void setPreventPortalCreation(boolean value) {
        this.preventPortalCreation = value;
    }

    public @NotNull Set<XMaterial> getPreventInteractionTypes() {
        return Collections.unmodifiableSet(this.preventInteractionSet);
    }

    public void setPreventInteractionTypes(@NotNull Collection<XMaterial> types) {
        this.preventInteractionSet.clear();
        this.preventInteractionSet.addAll(types);
    }

    public @NotNull Set<XMaterial> getPreventBreakingTypes() {
        return Collections.unmodifiableSet(this.preventBreakingSet);
    }

    public void setPreventBreakingTypes(@NotNull Collection<XMaterial> types) {
        this.preventBreakingSet.clear();
        this.preventBreakingSet.addAll(types);
    }

    public @NotNull Set<XMaterial> getPreventPlacingTypes() {
        return Collections.unmodifiableSet(this.preventPlacingSet);
    }

    public void setPreventPlacingTypes(@NotNull Collection<XMaterial> types) {
        this.preventPlacingSet.clear();
        this.preventPlacingSet.addAll(types);
    }

    @Override
    public boolean isPreventInteraction(@NotNull XMaterial blockType) {
        Set<XMaterial> typeSet = getPreventInteractionTypes();
        return typeSet.contains(blockType);
    }

    @Override
    public boolean isPreventBreaking(@NotNull XMaterial blockType) {
        Set<XMaterial> typeSet = getPreventBreakingTypes();
        return typeSet.contains(blockType);
    }

    @Override
    public boolean isPreventPlacing(@NotNull XMaterial blockType) {
        Set<XMaterial> typeSet = getPreventPlacingTypes();
        return typeSet.contains(blockType);
    }
}
