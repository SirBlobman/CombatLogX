package com.SirBlobman.combatlogx.expansion.compatibility.crackshot.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagReason;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.crackshot.CompatibilityCrackShot;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

public class ListenerCrackShot implements Listener {
    private final ICombatLogX plugin;
    public ListenerCrackShot(CompatibilityCrackShot expansion) {
        this.plugin = expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onShot(WeaponDamageEntityEvent e) {
        Entity entity = e.getVictim();
        if(!(entity instanceof LivingEntity)) return;

        LivingEntity victim = (LivingEntity) entity;
        Player shooter = e.getPlayer();

        ICombatManager manager = this.plugin.getCombatManager();
        manager.tag(shooter, victim, victim instanceof Player ? TagType.PLAYER : TagType.MOB, TagReason.ATTACKER);

        if(victim instanceof Player) {
            Player player = (Player) victim;
            manager.tag(player, shooter, TagType.PLAYER, TagReason.ATTACKED);
        }
    }
}