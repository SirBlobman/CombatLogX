package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListenerNPCMove extends BukkitRunnable {

    private final Map<UUID, Location> oldLocations = new HashMap<>();
    private final List<NoEntryHandler> noEntryHandlers = new ArrayList<>();

    public ListenerNPCMove(CompatibilityCitizens expansion) {
        ListenerNPCMove self = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                for(final Expansion enabledExpansion : expansion.getPlugin().getExpansionManager().getEnabledExpansions()) {
                    if(enabledExpansion instanceof NoEntryExpansion) {
                        NoEntryExpansion noEntryExpansion = (NoEntryExpansion) enabledExpansion;
                        self.noEntryHandlers.add(noEntryExpansion.getNoEntryHandler());
                    }
                }
                self.runTaskTimerAsynchronously(expansion.getPlugin().getPlugin(), 2, 2);
            }
        }.runTaskLater(expansion.getPlugin().getPlugin(), 1);
    }

    @Override
    public void run() {
        for(final UUID uuid : oldLocations.keySet()) {
            NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
            Location from = oldLocations.get(uuid);
            Location to = npc.getStoredLocation();
            boolean safe = isSafeZone(to);
            if(!(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) && safe) {
                npc.teleport(from, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            }
        }
    }

    public void registerNPC(NPC npc) {
        oldLocations.put(npc.getUniqueId(), npc.getStoredLocation());
    }

    public void unregisterNPC(NPC npc) {
        oldLocations.remove(npc.getUniqueId());
    }

    private boolean isSafeZone(Location location) {
        for(final NoEntryHandler noEntryHandler : noEntryHandlers) {
            if(noEntryHandler.isSafeZone(null, location, PlayerPreTagEvent.TagType.PLAYER)) return true;
        }
        return false;
    }
}
