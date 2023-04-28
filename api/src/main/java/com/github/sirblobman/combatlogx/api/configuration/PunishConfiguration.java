package com.github.sirblobman.combatlogx.api.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.combatlogx.api.object.KillTime;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnum;

public final class PunishConfiguration implements IConfigurable {
    private final List<String> kickIgnoreList;
    private final List<String> customDeathMessageList;
    private boolean onDisconnect;
    private boolean onKick;
    private boolean onExpire;
    private KillTime killTime;
    private boolean enablePunishmentCounter;
    private boolean kickIgnoreListInverted;

    public PunishConfiguration() {
        this.onDisconnect = true;
        this.onKick = false;
        this.onExpire = false;
        this.killTime = KillTime.QUIT;
        this.enablePunishmentCounter = true;
        this.kickIgnoreListInverted = false;

        this.kickIgnoreList = new ArrayList<>();
        this.customDeathMessageList = new ArrayList<>();
    }

    @Override
    public void load(ConfigurationSection config) {
        setOnDisconnect(config.getBoolean("on-disconnect", true));
        setOnKick(config.getBoolean("on-kick", false));
        setOnExpire(config.getBoolean("on-expire", false));
        setEnablePunishmentCounter(config.getBoolean("enable-punishment-counter"));

        String killTimeName = config.getString("kill-time", "QUIT");
        setKillTime(parseEnum(KillTime.class, killTimeName, KillTime.QUIT));

        setKickIgnoreList(config.getStringList("kick-ignore-list"));
        setCustomDeathMessages(config.getStringList("custom-death-message-list"));
    }

    public boolean isOnDisconnect() {
        return this.onDisconnect;
    }

    public void setOnDisconnect(boolean onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    public boolean isOnKick() {
        return this.onKick;
    }

    public void setOnKick(boolean onKick) {
        this.onKick = onKick;
    }

    public boolean isOnExpire() {
        return this.onExpire;
    }

    public void setOnExpire(boolean onExpire) {
        this.onExpire = onExpire;
    }

    public @NotNull KillTime getKillTime() {
        return this.killTime;
    }

    public void setKillTime(@NotNull KillTime killTime) {
        this.killTime = killTime;
    }

    public boolean isEnablePunishmentCounter() {
        return enablePunishmentCounter;
    }

    public void setEnablePunishmentCounter(boolean enablePunishmentCounter) {
        this.enablePunishmentCounter = enablePunishmentCounter;
    }

    public @NotNull List<String> getKickIgnoreList() {
        return Collections.unmodifiableList(this.kickIgnoreList);
    }

    public void setKickIgnoreList(@NotNull Collection<String> kickIgnores) {
        this.kickIgnoreList.clear();
        this.kickIgnoreList.addAll(kickIgnores);
    }

    public boolean isKickIgnoreListInverted() {
        return this.kickIgnoreListInverted;
    }

    public void setKickIgnoreListInverted(boolean inverted) {
        this.kickIgnoreListInverted = inverted;
    }

    public boolean isKickIgnored(@NotNull String reason) {
        boolean ignore = isInIgnoreList(reason);
        boolean inverted = isKickIgnoreListInverted();
        return  (inverted != ignore);
    }

    private boolean isInIgnoreList(@NotNull String reason) {
        List<String> kickIgnoreList = getKickIgnoreList();
        if (kickIgnoreList.contains("*")) {
            return true;
        }

        for (String part : kickIgnoreList) {
            if (reason.contains(part)) {
                return true;
            }
        }

        return false;
    }

    public List<String> getCustomDeathMessages() {
        return Collections.unmodifiableList(this.customDeathMessageList);
    }

    public void setCustomDeathMessages(@NotNull Collection<String> deathMessages) {
        this.customDeathMessageList.clear();
        this.customDeathMessageList.addAll(deathMessages);
    }
}
