package combatlogx.expansion.death.effects.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Item;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;

public final class ItemRemoveTask extends EntityTaskDetails<ConfigurablePlugin, Item> {
    public ItemRemoveTask(@NotNull ConfigurablePlugin plugin, @NotNull Item entity) {
        super(plugin, entity);
    }

    @Override
    public void run() {
        Item entity = getEntity();
        if (entity != null) {
            entity.remove();
        }
    }
}
