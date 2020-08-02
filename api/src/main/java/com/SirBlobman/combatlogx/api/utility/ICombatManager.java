package com.SirBlobman.combatlogx.api.utility;

import java.util.List;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface ICombatManager {
    boolean tag(Player player, LivingEntity enemy, PlayerPreTagEvent.TagType tagType, PlayerPreTagEvent.TagReason tagReason);
    void untag(Player player, PlayerUntagEvent.UntagReason untagReason);

    boolean isInCombat(Player player);
    List<Player> getPlayersInCombat();
    LivingEntity getEnemy(Player player);
    OfflinePlayer getByEnemy(LivingEntity enemy);

    int getTimerSecondsLeft(Player player);
    long getTimerMillisLeft(Player player);

    boolean punish(Player player, PlayerUntagEvent.UntagReason punishReason, LivingEntity previousEnemy);
    String getSudoCommand(Player player, LivingEntity enemy, String command);
}