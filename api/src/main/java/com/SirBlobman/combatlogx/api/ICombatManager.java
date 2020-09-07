package com.SirBlobman.combatlogx.api;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.object.TagReason;
import com.SirBlobman.combatlogx.api.object.TagType;
import com.SirBlobman.combatlogx.api.object.UntagReason;

public interface ICombatManager {
    boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason);
    boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason, long customEndMillis);
    void untag(Player player, UntagReason untagReason);

    boolean isInCombat(Player player);
    List<Player> getPlayersInCombat();

    LivingEntity getEnemy(Player player);
    OfflinePlayer getByEnemy(LivingEntity enemy);

    long getTimerLeftMillis(Player player);
    int getTimerLeftSeconds(Player player);

    boolean punish(Player player, UntagReason punishReason, LivingEntity previousEnemy);
    String replaceVariables(Player player, LivingEntity enemy, String string);
}