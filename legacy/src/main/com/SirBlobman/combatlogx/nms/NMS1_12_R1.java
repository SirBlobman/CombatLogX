package com.SirBlobman.combatlogx.nms;

import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class NMS1_12_R1 extends NMSUtil {
    @Override
    public void action(Player player, String msg) {
        String color = Util.color(msg);
        Spigot spigot = player.spigot();
        
        TextComponent actionText = new TextComponent(color);
        spigot.sendMessage(ChatMessageType.ACTION_BAR, actionText);
    }
}