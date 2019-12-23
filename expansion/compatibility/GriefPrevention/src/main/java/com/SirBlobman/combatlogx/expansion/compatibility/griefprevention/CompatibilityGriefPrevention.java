package com.SirBlobman.combatlogx.expansion.compatibility.griefprevention;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.NoEntryExpansion;

public class CompatibilityGriefPrevention extends NoEntryExpansion {
    public CompatibilityGriefPrevention(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityGriefPrevention";
    }

    @Override
    public String getName() {
        return "GriefPrevention Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public boolean canEnable() {
        return false;
    }

    @Override
    public void onActualEnable() {

    }

    @Override
    public NoEntryMode getNoEntryMode() {
        return null;
    }

    @Override
    public double getNoEntryKnockbackStrength() {
        return 0;
    }

    @Override
    public String getNoEntryMessage(TagType tagType) {
        return null;
    }

    @Override
    public int getNoEntryMessageCooldown() {
        return 0;
    }
}