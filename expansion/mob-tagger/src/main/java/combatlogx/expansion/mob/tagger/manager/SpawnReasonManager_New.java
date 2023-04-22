package combatlogx.expansion.mob.tagger.manager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;
import org.jetbrains.annotations.NotNull;

public final class SpawnReasonManager_New implements ISpawnReasonManager {
    private final MobTaggerExpansion expansion;
    private final NamespacedKey spawnReasonKey;

    public SpawnReasonManager_New(MobTaggerExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");

        ICombatLogX combatLogX = expansion.getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();
        this.spawnReasonKey = new NamespacedKey(plugin, "mob_tagger_spawn_reason");
    }

    private @NotNull NamespacedKey getSpawnReasonKey() {
        return this.spawnReasonKey;
    }

    @Override
    public @NotNull MobTaggerExpansion getExpansion() {
        return this.expansion;
    }

    @NotNull
    @Override
    public SpawnReason getSpawnReason(@NotNull Entity entity) {
        NamespacedKey spawnReasonKey = getSpawnReasonKey();
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        if (!dataContainer.has(spawnReasonKey, PersistentDataType.STRING)) {
            return SpawnReason.DEFAULT;
        }

        String spawnReasonName = dataContainer.get(spawnReasonKey, PersistentDataType.STRING);
        if (spawnReasonName == null) {
            return SpawnReason.DEFAULT;
        }

        try {
            return SpawnReason.valueOf(spawnReasonName);
        } catch(IllegalArgumentException ignored) {
            return SpawnReason.DEFAULT;
        }
    }

    @Override
    public void setSpawnReason(@NotNull Entity entity, @NotNull SpawnReason spawnReason) {
        String spawnReasonName = spawnReason.name();
        NamespacedKey spawnReasonKey = getSpawnReasonKey();
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        dataContainer.set(spawnReasonKey, PersistentDataType.STRING, spawnReasonName);
    }

    @Override
    public void clear() {
        // Empty Method
    }
}
