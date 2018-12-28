package com.SirBlobman.expansion.notcombatlogx;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notcombatlogx.config.ConfigNot;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.io.File;

public class NotCombatLogX implements CLXExpansion, Listener {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "NotCombatLogX";
    }

    public String getVersion() {
        return "13.2";
    }

    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigNot.load();
        PluginUtil.regEvents(this);
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {
        ConfigNot.load();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        World world = entity.getWorld();
        String worldName = world.getName();
        if(ConfigOptions.OPTION_DISABLED_WORLDS.contains(worldName)) return;
        
        if (entity instanceof Player) {
            Player player = (Player) entity;
            DamageCause cause = e.getCause();
            if (ConfigNot.canDamageTypeTagPlayer(cause)) {
                if (!CombatUtil.isInCombat(player)) {
                    String msg = ConfigNot.getTagMessage(cause);
                    Util.sendMessage(player, msg);
                    CombatUtil.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
                }
            }
        }
    }
}