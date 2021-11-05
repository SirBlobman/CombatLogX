package combatlogx.expansion.scoreboard.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.utility.Validate;

public final class CustomLine {
    private final ChatColor chatColor;
    private final Team team;
    private final int line;
    
    public CustomLine(ChatColor chatColor, Team team, int line) {
        this.chatColor = Validate.notNull(chatColor, "chatColor must not be null!");
        this.team = Validate.notNull(team, "team must not be null!");
        this.line = line;
    }
    
    public ChatColor getChatColor() {
        return this.chatColor;
    }
    
    public Team getTeam() {
        return this.team;
    }
    
    public int getLine() {
        return this.line;
    }
}
