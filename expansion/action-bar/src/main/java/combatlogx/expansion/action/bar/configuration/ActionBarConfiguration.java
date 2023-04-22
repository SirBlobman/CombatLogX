package combatlogx.expansion.action.bar.configuration;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.adventure.text.format.NamedTextColor;
import com.github.sirblobman.api.shaded.adventure.text.format.TextColor;

import org.jetbrains.annotations.NotNull;

public final class ActionBarConfiguration implements IConfigurable {
    private boolean enabled;
    private long scale;

    private String leftColorString;
    private String rightColorString;
    private String leftSymbol;
    private String rightSymbol;

    private transient TextColor leftColor;
    private transient TextColor rightColor;

    public ActionBarConfiguration() {
        setEnabled(true);
        setScale(15L);
        setLeftColorString("GREEN");
        setRightColorString("RED");
        setLeftSymbol("|");
        setRightSymbol("|");
    }

    @Override
    public void load(ConfigurationSection config) {
        setEnabled(config.getBoolean("enabled", true));
        setScale(config.getLong("scale", 15L));
        setLeftColorString(config.getString("left-color", "GREEN"));
        setRightColorString(config.getString("right-color", "RED"));
        setLeftSymbol(config.getString("left-symbol", "|"));
        setRightSymbol(config.getString("right-symbol", "|"));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getScale() {
        return this.scale;
    }

    public void setScale(long scale) {
        this.scale = scale;
    }

    public String getLeftColorString() {
        return this.leftColorString;
    }

    public void setLeftColorString(@NotNull String leftColorString) {
        this.leftColorString = leftColorString;
        this.leftColor = null;
    }

    public @NotNull String getRightColorString() {
        return this.rightColorString;
    }

    public void setRightColorString(@NotNull String rightColorString) {
        this.rightColorString = rightColorString;
        this.rightColor = null;
    }

    public @NotNull String getLeftSymbol() {
        return this.leftSymbol;
    }

    public void setLeftSymbol(@NotNull String leftSymbol) {
        this.leftSymbol = leftSymbol;
    }

    public @NotNull String getRightSymbol() {
        return rightSymbol;
    }

    public void setRightSymbol(@NotNull String rightSymbol) {
        this.rightSymbol = rightSymbol;
    }

    public @NotNull TextColor getLeftColor() {
        if (this.leftColor != null) {
            return this.leftColor;
        }

        String leftColorString = getLeftColorString();
        return (this.leftColor = parseTextColor(leftColorString, NamedTextColor.GREEN));
    }

    public @NotNull TextColor getRightColor() {
        if (this.rightColor != null) {
            return this.rightColor;
        }

        String rightColorString = getRightColorString();
        return (this.rightColor = parseTextColor(rightColorString, NamedTextColor.RED));
    }

    private @NotNull TextColor parseTextColor(@NotNull String colorString, @NotNull TextColor defaultColor) {
        if (colorString.isEmpty()) {
            return defaultColor;
        }

        if (colorString.startsWith("#") && colorString.length() == 7) {
            try {
                String colorHex = colorString.substring(1);
                int colorInt = Integer.parseInt(colorHex, 16);
                return TextColor.color(colorInt);
            } catch (IllegalArgumentException ex) {
                return defaultColor;
            }
        }

        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(colorString);
        return (namedTextColor != null ? namedTextColor : defaultColor);
    }
}
