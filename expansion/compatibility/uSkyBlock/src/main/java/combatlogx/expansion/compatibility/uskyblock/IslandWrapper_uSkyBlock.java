package combatlogx.expansion.compatibility.uskyblock;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;

import us.talabrek.ultimateskyblock.api.IslandInfo;

public class IslandWrapper_uSkyBlock extends IslandWrapper {
    private final IslandInfo island;
    private final Map<String, UUID> nameCache;

    public IslandWrapper_uSkyBlock(@NotNull IslandInfo island) {
        this.island = island;
        this.nameCache = new HashMap<>();
    }

    private @NotNull IslandInfo getIsland() {
        return this.island;
    }

    @Override
    public @NotNull Set<UUID> getMembers() {
        IslandInfo island = getIsland();
        Set<String> memberNameSet = island.getMembers();

        Set<UUID> memberSet = new HashSet<>();
        for (String memberName : memberNameSet) {
            UUID memberId = getPlayerId(memberName);
            memberSet.add(memberId);
        }

        return Collections.unmodifiableSet(memberSet);
    }

    private @NotNull UUID getPlayerId(@NotNull String playerName) {
        return this.nameCache.computeIfAbsent(playerName, this::fetchPlayerId);
    }

    @SuppressWarnings("deprecation")
    private @NotNull UUID fetchPlayerId(@NotNull String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        return offlinePlayer.getUniqueId();
    }
}
