package com.SirBlobman.expansion.compatcitizens;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.listener.NPCManager;
import com.SirBlobman.expansion.compatcitizens.listener.NPCManager.TraitCombatLogX;

import java.io.File;

import org.bukkit.OfflinePlayer;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;

public class CompatCitizens implements CLXExpansion {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "CompatCitizens";
    }

    public String getName() {
        return "Citizens Compatibility";
    }

    public String getVersion() {
        return "13.3";
    }

    @Override
    public void enable() {
        if (PluginUtil.isEnabled("Citizens")) {
            FOLDER = getDataFolder();
            ConfigCitizens.load();
            
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraitCombatLogX.class));
            PluginUtil.regEvents(new NPCManager());
        } else {
            String error = "Citizens is not installed, removing expansion....";
            print(error);
            Expansions.unloadExpansion(this);
        }
    }

    @Override
    public void disable() {
        if (PluginUtil.isEnabled("Citizens")) {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                if(npc.hasTrait(TraitCombatLogX.class)) {
                    TraitCombatLogX clxTrait = npc.getTrait(TraitCombatLogX.class);
                    OfflinePlayer op = clxTrait.getOfflinePlayer();
                    if(op != null) NPCManager.removeNPC(op);
                }
            }
        }
    }

    @Override
    public void onConfigReload() {
        if (PluginUtil.isEnabled("Citizens")) {
            ConfigCitizens.load();
        }
    }
}