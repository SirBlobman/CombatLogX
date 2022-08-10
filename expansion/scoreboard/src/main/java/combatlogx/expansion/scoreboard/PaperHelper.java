package combatlogx.expansion.scoreboard;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.adventure.adventure.text.Component;

import combatlogx.expansion.scoreboard.scoreboard.CustomLine;

public final class PaperHelper {
    public static void updateTitle(Objective objective, Component shadedComponent) {
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
