package com.SirBlobman.combatlogx.expansion.notifier.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class CustomLine {
    private final ChatColor color;
    private final int line;
    private final Team team;

    public CustomLine(ChatColor color, int line, Team team) {
        this.color = color;
        this.line = line;
        this.team = team;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public int getLine() {
        return this.line;
    }

    public Team getTeam() {
        return this.team;
    }
}