package combatlogx.expansion.scoreboard.scoreboard;

import org.jetbrains.annotations.NotNull;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.utility.paper.ComponentConverter;
import com.github.sirblobman.api.shaded.adventure.text.Component;

public final class PaperScoreboard {
    public static void setTitle(@NotNull Objective objective, @NotNull Component shadedComponent) {
        net.kyori.adventure.text.Component normalComponent = ComponentConverter.shadedToNormal(shadedComponent);
        objective.displayName(normalComponent);
    }

    public static void setLine(@NotNull CustomLine customLine, @NotNull Component shadedComponent) {
        net.kyori.adventure.text.Component prefix = ComponentConverter.shadedToNormal(shadedComponent);
        net.kyori.adventure.text.Component suffix = net.kyori.adventure.text.Component.empty();

        Team team = customLine.getTeam();
        team.prefix(prefix);
        team.suffix(suffix);
    }
}
