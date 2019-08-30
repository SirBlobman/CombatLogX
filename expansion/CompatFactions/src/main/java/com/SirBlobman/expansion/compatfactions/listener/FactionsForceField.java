package com.SirBlobman.expansion.compatfactions.listener;

import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.olivolja3.force.field.ForceField;
import com.SirBlobman.expansion.compatfactions.CompatFactions;
import com.SirBlobman.expansion.compatfactions.config.ConfigFactions;
import com.SirBlobman.expansion.compatfactions.utility.FactionsUtil;
import com.massivecraft.factions.Factions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FactionsForceField extends ForceField {
    private final FactionsUtil FUTIL;
    public FactionsForceField(CompatFactions factions, FactionsUtil futil) {
        super(factions);
        this.FUTIL = futil;
    }

    @Override
    public boolean isSafe(Location location, Player player, PlayerTagEvent.TagType tagType) {
        return isSafe(location, player);
    }

    @Override
    public boolean isSafe(Location location, Player player) {
        return FUTIL.isSafeZone(location);
    }

    @Override
    public boolean canBypass(Player player) {
        return player.hasPermission(ConfigFactions.FORCEFIELD_BYPASS_PERMISSION);
    }

    @Override
    public Material getForceFieldMaterial() {
        return ConfigFactions.FORCEFIELD_MATERIAL;
    }

    @Override
    public int getForceFieldMaterialData() {
        return ConfigFactions.FORCEFIELD_MATERIAL_DATA;
    }

    @Override
    public int getForceFieldRadius() {
        return ConfigFactions.FORCEFIELD_SIZE;
    }
}