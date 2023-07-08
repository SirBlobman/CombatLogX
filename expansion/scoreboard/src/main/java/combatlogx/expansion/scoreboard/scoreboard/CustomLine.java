package combatlogx.expansion.scoreboard.scoreboard;

import org.jetbrains.annotations.NotNull;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public final class CustomLine {
    private final ChatColor color;
    private final Team team;
    private final int line;

    public CustomLine(@NotNull ChatColor color, @NotNull Team team, int line) {
        this.color = color;
        this.team = team;
        this.line = line;
    }

    public @NotNull ChatColor getColor() {
        return this.color;
    }

    public @NotNull Team getTeam() {
        return this.team;
    }

    public int getLine() {
        return this.line;
    }
}
