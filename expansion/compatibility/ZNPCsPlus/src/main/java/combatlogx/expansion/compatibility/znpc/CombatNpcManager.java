package combatlogx.expansion.compatibility.znpc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.compatibility.znpc.configuration.NpcConfiguration;
import combatlogx.expansion.compatibility.znpc.task.NpcRemoveTask;
import lol.pyr.znpcsplus.api.NpcApi;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.api.entity.EntityPropertyRegistry;
import lol.pyr.znpcsplus.api.hologram.Hologram;
import lol.pyr.znpcsplus.api.npc.Npc;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.npc.NpcRegistry;
import lol.pyr.znpcsplus.api.npc.NpcType;
import lol.pyr.znpcsplus.util.NpcLocation;

public final class CombatNpcManager {
    private final ZNPCExpansion expansion;
    private final Map<UUID, CombatNpc> playerNpcMap;
    private final Map<UUID, CombatNpc> npcCombatMap;

    public CombatNpcManager(@NotNull ZNPCExpansion expansion) {
        this.expansion = expansion;
        this.playerNpcMap = new ConcurrentHashMap<>();
        this.npcCombatMap = new ConcurrentHashMap<>();
    }

    private @NotNull ZNPCExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull ICombatLogX getCombatLogX() {
        return getExpansion().getPlugin();
    }

    public @NotNull YamlConfiguration getData(@NotNull OfflinePlayer player) {
        PlayerDataManager playerDataManager = getCombatLogX().getPlayerDataManager();
        return playerDataManager.get(player);
    }

    public void saveData(@NotNull OfflinePlayer player) {
        PlayerDataManager playerDataManager = getCombatLogX().getPlayerDataManager();
        playerDataManager.save(player);
    }

    public @NotNull CombatNpc createCombatNpc(@NotNull Player player, @NotNull List<Entity> enemyList) {
        if (player.hasMetadata("NPC")) {
            throw new IllegalArgumentException("Attempted to create NPC for an existing NPC.");
        }

        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation();

        NpcConfiguration npcConfiguration = getExpansion().getNpcConfiguration();
        EntityType entityType = npcConfiguration.getMobType();
        NpcApi npcApi = NpcApiProvider.get();
        NpcRegistry npcRegistry = npcApi.getNpcRegistry();

        NpcType npcType = npcApi.getNpcTypeRegistry().getByName(entityType.name());
        NpcLocation npcLocation = new NpcLocation(playerLocation);
        NpcEntry npcEntry = npcRegistry.create("combatnpc-" + playerId, playerWorld, npcType, npcLocation);
        npcEntry.setAllowCommandModification(false);
        npcEntry.setSave(false);

        String npcNameFormat = npcConfiguration.getCustomNpcNameFormat();
        String npcName = npcNameFormat.replace("{player_name}", playerName);

        Npc npc = npcEntry.getNpc();
        Hologram hologram = npc.getHologram();
        hologram.clearLines();
        hologram.addLine(npcName);
        npc.setEnabled(true);

        CombatNpc combatNpc = new CombatNpc(getExpansion(), npc, player);
        this.playerNpcMap.put(playerId, combatNpc);
        this.npcCombatMap.put(npc.getUuid(), combatNpc);

        if (!enemyList.isEmpty()) {
            Entity mainEnemy = enemyList.get(0);
            if (mainEnemy instanceof Player) {
                combatNpc.setEnemy((Player) mainEnemy);
            }
        }

        saveLocation(player, npc);
        saveInventory(player);
        equipNpc(player, npc);
        combatNpc.start();
        return combatNpc;
    }

    public @Nullable CombatNpc getCombatNpc(@Nullable NPC npc) {
        if (npc == null) {
            return null;
        }

        UUID npcId = npc.getUniqueId();
        return this.npcCombatMap.get(npcId);
    }

    public @Nullable CombatNpc getCombatNpc(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        return this.playerNpcMap.get(playerId);
    }

    public void remove(@NotNull CombatNpc combatNpc) {
        try {
            combatNpc.cancel();
        } catch(IllegalStateException ignored) {
            // Do Nothing
        }

        UUID ownerId = combatNpc.getOwnerId();
        OfflinePlayer owner = combatNpc.getOfflineOwner();
        Npc originalNpc = combatNpc.getOriginalNpc();
        saveNpc(owner, originalNpc);

        this.playerNpcMap.remove(ownerId);
        this.npcCombatMap.remove(originalNpc.getUuid());

        TaskScheduler scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        scheduler.scheduleTask(new NpcRemoveTask(getExpansion(), originalNpc));
    }

    public void removeAll() {
        Map<UUID, CombatNpc> copyMap = new HashMap<>(this.playerNpcMap);
        this.playerNpcMap.clear();

        Collection<CombatNpc> combatNpcCollection = copyMap.values();
        for (CombatNpc combatNpc : combatNpcCollection) {
            remove(combatNpc);
        }
    }

    private void saveNpc(@NotNull OfflinePlayer player, @NotNull Npc npc) {
        saveHealth(player, npc);
        saveLocation(player, npc);

        YamlConfiguration data = getData(player);
        data.set("znpcs-compatibility.punish", true);
        saveData(player);
    }

    private void saveHealth(@NotNull OfflinePlayer player, @NotNull Npc npc) {
        // ZNPCs don't have health.
    }

    private void saveLocation(@NotNull OfflinePlayer player, @NotNull Npc npc) {
        World world = npc.getWorld();
        NpcLocation npcLocation = npc.getLocation();
        Location location = npcLocation.toBukkitLocation(world);

        YamlConfiguration data = getData(player);
        data.set("znpcs-compatibility.location", location);
        saveData(player);
    }
}
