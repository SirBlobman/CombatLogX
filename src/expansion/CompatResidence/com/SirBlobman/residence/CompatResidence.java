package com.SirBlobman.residence;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.preciousstones.StonesUtil;
import com.SirBlobman.residence.config.ConfigPreciousStones;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.io.File;

public class CompatResidence implements CLXExpansion, Listener {
    public static File FOLDER;

    @Override
    public void enable() {
        if (PluginUtil.isPluginEnabled("PreciousStones", "Phaed")) {
            FOLDER = getDataFolder();
            ConfigPreciousStones.load();
            Util.regEvents(this);
        } else {
            String error = "PreciousStones is not installed. This expansion is useless!";
            print(error);
        }
    }

    public String getUnlocalizedName() {
        return "CompatPreciousStones";
    }

    public String getName() {
        return "PreciousStones Compatability";
    }

    public String getVersion() {
        return "2";
    }
    
}