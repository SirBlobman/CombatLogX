package com.SirBlobman.expansion.notcombatlogx;

import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notcombatlogx.config.ConfigNot;
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
        return "13.1";
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
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player p = (Player) en;
            DamageCause dc = e.getCause();
            if (ConfigNot.canDamageTypeTagPlayer(dc)) {
                if (!CombatUtil.isInCombat(p)) {
                    String msg = ConfigNot.getTagMessage(dc);
                    Util.sendMessage(p, msg);
                    CombatUtil.tag(p, null, TagType.UNKNOWN, TagReason.UNKNOWN);
                }
            }
        }
    }
}