package combatlogx.expansion.mob.tagger.manager;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;
import org.jetbrains.annotations.NotNull;

public interface ISpawnReasonManager {
    MobTaggerExpansion getExpansion();

    @NotNull SpawnReason getSpawnReason(Entity entity);

    void setSpawnReason(Entity entity, SpawnReason spawnReason);

    void clear();
}
