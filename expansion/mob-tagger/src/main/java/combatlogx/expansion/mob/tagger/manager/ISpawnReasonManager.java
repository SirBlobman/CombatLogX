package combatlogx.expansion.mob.tagger.manager;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;

public interface ISpawnReasonManager {
    MobTaggerExpansion getExpansion();

    SpawnReason getSpawnReason(Entity entity);
    void setSpawnReason(Entity entity, SpawnReason spawnReason);
    void clear();
}
