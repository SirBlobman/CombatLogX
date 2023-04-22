package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.configuration.ConfigurationSection;

public final class ChatConfiguration implements IChatConfiguration {
    private boolean disableChat;

    public ChatConfiguration() {
        this.disableChat = false;
    }

    @Override
    public void load(ConfigurationSection config) {
        setDisableChat(config.getBoolean("disable-chat", false));
    }

    @Override
    public boolean isDisableChat() {
        return this.disableChat;
    }

    public void setDisableChat(boolean disableChat) {
        this.disableChat = disableChat;
    }
}
