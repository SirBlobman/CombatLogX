package combatlogx.expansion.mob.tagger.manager;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;
import org.jetbrains.annotations.NotNull;

public interface ISpawnReasonManager {
    @NotNull MobTaggerExpansion getExpansion();

    @NotNull SpawnReason getSpawnReason(@NotNull Entity entity);

    void setSpawnReason(@NotNull Entity entity, @NotNull SpawnReason spawnReason);

    void clear();
}
