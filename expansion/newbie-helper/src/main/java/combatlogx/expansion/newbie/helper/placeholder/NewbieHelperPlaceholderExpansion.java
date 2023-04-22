package combatlogx.expansion.newbie.helper.placeholder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;
import com.github.sirblobman.api.shaded.adventure.text.Component;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class NewbieHelperPlaceholderExpansion implements IPlaceholderExpansion {
    private final NewbieHelperExpansion expansion;

    public NewbieHelperPlaceholderExpansion(@NotNull NewbieHelperExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public @NotNull ICombatLogX getCombatLogX() {
        NewbieHelperExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    @Override
    public @NotNull String getId() {
        return "newbie";
    }

    @Override
    public @Nullable Component getReplacement(@NotNull Player player, @NotNull List<Entity> enemyList,
                                              @NotNull String placeholder) {
        switch (placeholder) {
            case "helper_pvp_status":
                return getPvpStatus(player);
            case "helper_protected":
                return getProtected(player);
            case "helper_protection_time_left":
                return getProtectionTimeLeft(player);
            default:
                break;
        }

        return null;
    }

    private @NotNull NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull Component getPvpStatus(@NotNull Player player) {
        NewbieHelperExpansion expansion = getExpansion();
        PVPManager pvpManager = expansion.getPVPManager();
        boolean pvp = !pvpManager.isDisabled(player);

        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        String messagePath = ("placeholder.pvp-status." + (pvp ? "enabled" : "disabled"));
        return languageManager.getMessage(player, messagePath);
    }

    private @NotNull Component getProtected(@NotNull Player player) {
        NewbieHelperExpansion expansion = getExpansion();
        ProtectionManager protectionManager = expansion.getProtectionManager();
        boolean isProtected = protectionManager.isProtected(player);
        return Component.text(isProtected);
    }

    private @NotNull Component getProtectionTimeLeft(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        Component zero = languageManager.getMessage(player, "placeholder.time-left-zero");

        NewbieHelperExpansion expansion = getExpansion();
        ProtectionManager protectionManager = expansion.getProtectionManager();
        if (!protectionManager.isProtected(player)) {
            return zero;
        }

        long expireTime = protectionManager.getProtectionExpireTime(player);
        long systemTime = System.currentTimeMillis();
        long timeLeftMillis = (expireTime - systemTime);
        if (timeLeftMillis <= 0L) {
            return zero;
        }

        long timeLeftSeconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
        return Component.text(timeLeftSeconds);
    }
}
