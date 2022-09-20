package combatlogx.expansion.scoreboard.scoreboard;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.utility.paper.ComponentConverter;

public final class PaperScoreboard {
    public static void setTitle(Objective objective, Component shadedComponent) {
        net.kyori.adventure.text.Component normalComponent = ComponentConverter.shadedToNormal(shadedComponent);
        objective.displayName(normalComponent);
    }

    public static void setLine(CustomLine customLine, Component shadedComponent) {
        net.kyori.adventure.text.Component prefix = ComponentConverter.shadedToNormal(shadedComponent);
        net.kyori.adventure.text.Component suffix = net.kyori.adventure.text.Component.empty();

        Team team = customLine.getTeam();
        team.prefix(prefix);
        team.suffix(suffix);
    }
}
