package com.SirBlobman.combatlogx.expansion.compatibility.factions.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.olivolja3.force.field.ForceField;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.CompatibilityFactions;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.FactionsHook;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.HookForceField;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ListenerForceField extends ForceField {
    private final FactionsHook factionsHook;
    public ListenerForceField(CompatibilityFactions expansion, FactionsHook factionsHook) {
        super(expansion);
        this.factionsHook = factionsHook;
    }

    @Override
    public boolean isSafe(Location location, Player player, PlayerPreTagEvent.TagType tagType) {
        return isSafe(location, player);
    }

    @Override
    public boolean isSafe(Location location, Player player) {
        return this.factionsHook.isSafeZone(location);
    }

    @Override
    public boolean canBypass(Player player) {
        String permission = HookForceField.getBypassPermission();
        return player.hasPermission(permission);
    }

    @Override
    public Material getForceFieldMaterial() {
        return HookForceField.getForceFieldMaterial();
    }

    @Override
    public int getForceFieldMaterialData() {
        return HookForceField.getForceFieldMaterialData();
    }

    @Override
    public int getForceFieldRadius() {
        return HookForceField.getForceFieldRadius();
    }
}
