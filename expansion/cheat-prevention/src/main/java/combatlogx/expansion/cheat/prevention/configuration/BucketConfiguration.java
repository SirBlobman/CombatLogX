package combatlogx.expansion.cheat.prevention.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.shaded.xseries.XMaterial;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class BucketConfiguration implements IBucketConfiguration {
    private final Set<XMaterial> preventBucketEmptyTypeSet;
    private final Set<XMaterial> preventBucketFillTypeSet;
    private boolean preventBucketEmpty;
    private boolean preventBucketFill;

    public BucketConfiguration() {
        this.preventBucketEmpty = false;
        this.preventBucketFill = false;
        this.preventBucketEmptyTypeSet = EnumSet.noneOf(XMaterial.class);
        this.preventBucketFillTypeSet = EnumSet.noneOf(XMaterial.class);
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventBucketEmpty(config.getBoolean("prevent-bucket-empty", false));
        setPreventBucketFill(config.getBoolean("prevent-bucket-fill", false));

        setPreventBucketEmptyTypes(parseEnums(config.getStringList("prevent-bucket-empty-list"), XMaterial.class));
        setPreventBucketFillTypes(parseEnums(config.getStringList("prevent-bucket-fill-list"), XMaterial.class));
    }

    @Override
    public boolean isPreventBucketEmpty() {
        return this.preventBucketEmpty;
    }

    public void setPreventBucketEmpty(boolean value) {
        this.preventBucketEmpty = value;
    }

    @Override
    public boolean isPreventBucketFill() {
        return this.preventBucketFill;
    }

    public void setPreventBucketFill(boolean value) {
        this.preventBucketFill = value;
    }

    public @NotNull Set<XMaterial> getPreventBucketEmptyTypes() {
        return Collections.unmodifiableSet(this.preventBucketEmptyTypeSet);
    }

    public void setPreventBucketEmptyTypes(@NotNull Collection<XMaterial> types) {
        this.preventBucketEmptyTypeSet.clear();
        this.preventBucketEmptyTypeSet.addAll(types);
    }

    public @NotNull Set<XMaterial> getPreventBucketFillTypes() {
        return Collections.unmodifiableSet(this.preventBucketFillTypeSet);
    }

    public void setPreventBucketFillTypes(@NotNull Collection<XMaterial> types) {
        this.preventBucketFillTypeSet.clear();
        this.preventBucketFillTypeSet.addAll(types);
    }

    @Override
    public boolean isPreventEmpty(@NotNull XMaterial material) {
        Set<XMaterial> typeSet = getPreventBucketEmptyTypes();
        return typeSet.contains(material);
    }

    @Override
    public boolean isPreventFill(@NotNull XMaterial material) {
        Set<XMaterial> typeSet = getPreventBucketFillTypes();
        return typeSet.contains(material);
    }
}
