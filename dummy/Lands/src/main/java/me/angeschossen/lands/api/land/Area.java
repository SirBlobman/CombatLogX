package me.angeschossen.lands.api.land;

import org.bukkit.entity.Player;

import me.angeschossen.lands.api.role.enums.RoleSetting;

public interface Area {
    boolean canSetting(Player player, RoleSetting action, boolean sendMessage);
}