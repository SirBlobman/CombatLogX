package combatlogx.expansion.cheat.prevention.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryType;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class InventoryConfiguration implements IInventoryConfiguration {
    private final Set<InventoryType> noCloseMessageTypeSet;
    private boolean close;
    private boolean closeOnRetag;
    private boolean preventOpening;

    public InventoryConfiguration() {
        this.close = true;
        this.closeOnRetag = true;
        this.preventOpening = true;
        this.noCloseMessageTypeSet = EnumSet.noneOf(InventoryType.class);
    }

    @Override
    public void load(ConfigurationSection config) {
        setClose(config.getBoolean("close", true));
        setCloseOnRetag(config.getBoolean("close-on-retag", true));
        setPreventOpening(config.getBoolean("prevent-opening", true));

        List<String> inventoryTypeNameList = config.getStringList("no-close-message-type-list");
        setNoCloseMessageTypes(parseEnums(inventoryTypeNameList, InventoryType.class));
    }

    @Override
    public boolean isClose() {
        return this.close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    @Override
    public boolean isCloseOnRetag() {
        return this.closeOnRetag;
    }

    public void setCloseOnRetag(boolean closeOnRetag) {
        this.closeOnRetag = closeOnRetag;
    }

    @Override
    public boolean isPreventOpening() {
        return this.preventOpening;
    }

    public void setPreventOpening(boolean preventOpening) {
        this.preventOpening = preventOpening;
    }

    public @NotNull Set<InventoryType> getNoCloseMessageTypes() {
        return Collections.unmodifiableSet(this.noCloseMessageTypeSet);
    }

    public void setNoCloseMessageTypes(@NotNull Collection<InventoryType> types) {
        this.noCloseMessageTypeSet.clear();
        this.noCloseMessageTypeSet.addAll(types);
    }

    @Override
    public boolean isNoMessage(@NotNull InventoryType type) {
        Set<InventoryType> typeSet = getNoCloseMessageTypes();
        return typeSet.contains(type);
    }
}
