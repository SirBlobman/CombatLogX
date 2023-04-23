package combatlogx.expansion.mob.tagger.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;

public final class SpawnReasonManager_Legacy implements ISpawnReasonManager {
    private final MobTaggerExpansion expansion;
    private final Map<UUID, SpawnReason> spawnReasonMap;

    public SpawnReasonManager_Legacy(@NotNull MobTaggerExpansion expansion) {
        this.expansion = expansion;
        this.spawnReasonMap = new HashMap<>();
    }

    @Override
    public @NotNull MobTaggerExpansion getExpansion() {
        return this.expansion;
    }

    @NotNull
    @Override
    public SpawnReason getSpawnReason(@NotNull Entity entity) {
        UUID entityId = entity.getUniqueId();
        return this.spawnReasonMap.getOrDefault(entityId, SpawnReason.DEFAULT);
    }

    @Override
    public void setSpawnReason(@NotNull Entity entity, @NotNull SpawnReason spawnReason) {
        UUID entityId = entity.getUniqueId();
        this.spawnReasonMap.put(entityId, spawnReason);
    }

    @Override
    public void clear() {
        this.spawnReasonMap.clear();
    }
}
