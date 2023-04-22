package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.configuration.ConfigurationSection;

public final class CheatPreventionConfiguration implements IConfiguration {
    private int messageCooldown;

    public CheatPreventionConfiguration() {
        this.messageCooldown = 30;
    }

    @Override
    public void load(ConfigurationSection config) {
        setMessageCooldown(config.getInt("message-cooldown", 30));
    }

    @Override
    public int getMessageCooldown() {
        return this.messageCooldown;
    }

    public void setMessageCooldown(int messageCooldown) {
        this.messageCooldown = messageCooldown;
    }
}
