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
import org.jetbrains.annotations.Nullable;

public final class SpawnReasonManager_New implements ISpawnReasonManager {
    private final MobTaggerExpansion expansion;

    public SpawnReasonManager_New(MobTaggerExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    private JavaPlugin getPlugin() {
        MobTaggerExpansion expansion = getExpansion();
        ICombatLogX combatLogX = expansion.getPlugin();
        return combatLogX.getPlugin();
    }

    private NamespacedKey getSpawnReasonKey() {
        JavaPlugin plugin = getPlugin();
        return new NamespacedKey(plugin, "mob_tagger_spawn_reason");
    }

    @Override
    public MobTaggerExpansion getExpansion() {
        return this.expansion;
    }

    @Nullable
    @Override
    public SpawnReason getSpawnReason(Entity entity) {
        Validate.notNull(entity, "entity must not be null!");

        NamespacedKey spawnReasonKey = getSpawnReasonKey();
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        if(persistentDataContainer.has(spawnReasonKey, PersistentDataType.STRING)) {
            String spawnReasonName = persistentDataContainer.get(spawnReasonKey, PersistentDataType.STRING);
            try {
                return SpawnReason.valueOf(spawnReasonName);
            } catch(IllegalArgumentException ex) {
                return SpawnReason.DEFAULT;
            }
        }

        return null;
    }

    @Override
    public void setSpawnReason(Entity entity, SpawnReason spawnReason) {
        Validate.notNull(entity, "entity must not be null!");
        Validate.notNull(spawnReason, "spawnReason must not be null!");

        String spawnReasonName = spawnReason.name();
        NamespacedKey spawnReasonKey = getSpawnReasonKey();
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        persistentDataContainer.set(spawnReasonKey, PersistentDataType.STRING, spawnReasonName);
    }

    @Override
    public void clear() {
        // Do Nothing
    }
}
