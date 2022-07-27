package combatlogx.expansion.mob.tagger.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;
import org.jetbrains.annotations.NotNull;

public final class SpawnReasonManager_Legacy implements ISpawnReasonManager {
    private final MobTaggerExpansion expansion;
    private final Map<UUID, SpawnReason> spawnReasonMap;

    public SpawnReasonManager_Legacy(MobTaggerExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.spawnReasonMap = new HashMap<>();
    }

    @Override
    public MobTaggerExpansion getExpansion() {
        return this.expansion;
    }

    @NotNull
    @Override
    public SpawnReason getSpawnReason(Entity entity) {
        Validate.notNull(entity, "entity must not be null!");

        UUID entityId = entity.getUniqueId();
        return this.spawnReasonMap.getOrDefault(entityId, SpawnReason.DEFAULT);
    }

    @Override
    public void setSpawnReason(Entity entity, SpawnReason spawnReason) {
        Validate.notNull(entity, "entity must not be null!");
        Validate.notNull(spawnReason, "spawnReason must not be null!");

        UUID entityId = entity.getUniqueId();
        this.spawnReasonMap.put(entityId, spawnReason);
    }

    @Override
    public void clear() {
        this.spawnReasonMap.clear();
    }
}
