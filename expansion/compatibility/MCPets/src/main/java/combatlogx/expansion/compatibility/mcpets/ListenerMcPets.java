package combatlogx.expansion.compatibility.mcpets;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.events.PetSpawnEvent;

public final class ListenerMcPets extends ExpansionListener {
    public ListenerMcPets(@NotNull McPetsExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetSpawn(PetSpawnEvent e) {
        Pet pet = e.getPet();
        UUID ownerId = pet.getOwner();
        Player owner = Bukkit.getPlayer(ownerId);
        if (owner == null || !isInCombat(owner)) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        Pet activePet = MCPetsAPI.getActivePet(playerId);
        if (activePet != null) {
            activePet.despawn(PetDespawnReason.DONT_HAVE_PERM);
        }
    }
}
