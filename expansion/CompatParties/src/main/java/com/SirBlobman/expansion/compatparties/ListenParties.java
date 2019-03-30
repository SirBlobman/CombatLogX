package com.SirBlobman.expansion.compatparties;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;

import java.util.List;
import java.util.UUID;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;

public class ListenParties implements Listener {
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        LivingEntity enemy = e.getEnemy();
        if(!(enemy instanceof Player)) return;
        
        Player playerEnemy = (Player) enemy;
        UUID playerUUID = player.getUniqueId();
        UUID enemyUUID = playerEnemy.getUniqueId();
        
        PartiesAPI partyAPI = Parties.getApi();
        PartyPlayer partyPlayer = partyAPI.getPartyPlayer(playerUUID);
        if(partyPlayer == null) return;
        
        String partyName = partyPlayer.getPartyName();
        Party party = partyAPI.getParty(partyName);
        if(party == null) return;
        if(!party.isFriendlyFireProtected()) return;
        
        List<UUID> partyMembers = party.getMembers();
        if(partyMembers.contains(enemyUUID)) e.setCancelled(true);
    }
}