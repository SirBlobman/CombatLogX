package com.SirBlobman.combatlogx.api.utility;

import java.util.List;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface ICombatManager {
    public boolean tag(Player player, LivingEntity enemy, PlayerPreTagEvent.TagType tagType, PlayerPreTagEvent.TagReason tagReason);
    public void untag(Player player, PlayerUntagEvent.UntagReason untagReason);

    public boolean isInCombat(Player player);
    public List<Player> getPlayersInCombat();
    public LivingEntity getEnemy(Player player);
    public OfflinePlayer getByEnemy(LivingEntity enemy);

    public int getTimerSecondsLeft(Player player);
    public long getTimerMillisLeft(Player player);

    public boolean punish(Player player, PlayerUntagEvent.UntagReason punishReason, LivingEntity previousEnemy);
    public String getSudoCommand(Player player, LivingEntity enemy, String command);
}