package combatlogx.expansion.compatibility.essentials;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class EssentialsExpansionConfiguration implements IConfigurable {
    private boolean preventTeleportRequest;

    public EssentialsExpansionConfiguration() {
        this.preventTeleportRequest = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setPreventTeleportRequest(config.getBoolean("prevent-teleport-request", true));
    }

    public boolean isPreventTeleportRequest() {
        return this.preventTeleportRequest;
    }

    public void setPreventTeleportRequest(boolean preventTeleportRequest) {
        this.preventTeleportRequest = preventTeleportRequest;
    }
}
