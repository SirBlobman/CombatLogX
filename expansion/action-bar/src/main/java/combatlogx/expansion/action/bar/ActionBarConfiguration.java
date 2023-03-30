package combatlogx.expansion.action.bar;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.shaded.adventure.text.format.NamedTextColor;
import com.github.sirblobman.api.shaded.adventure.text.format.TextColor;
import com.github.sirblobman.api.configuration.IConfigurable;

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
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getScale() {
        return scale;
    }

    public void setScale(long scale) {
        this.scale = scale;
    }

    public String getLeftColorString() {
        return leftColorString;
    }

    public void setLeftColorString(String leftColorString) {
        this.leftColorString = leftColorString;
        this.leftColor = null;
    }

    public String getRightColorString() {
        return rightColorString;
    }

    public void setRightColorString(String rightColorString) {
        this.rightColorString = rightColorString;
        this.rightColor = null;
    }

    public String getLeftSymbol() {
        return leftSymbol;
    }

    public void setLeftSymbol(String leftSymbol) {
        this.leftSymbol = leftSymbol;
    }

    public String getRightSymbol() {
        return rightSymbol;
    }

    public void setRightSymbol(String rightSymbol) {
        this.rightSymbol = rightSymbol;
    }

    @NotNull
    public TextColor getLeftColor() {
        if (this.leftColor != null) {
            return this.leftColor;
        }

        String leftColorString = getLeftColorString();
        return (this.leftColor = parseTextColor(leftColorString, NamedTextColor.GREEN));
    }

    @NotNull
    public TextColor getRightColor() {
        if (this.rightColor != null) {
            return this.rightColor;
        }

        String rightColorString = getRightColorString();
        return (this.rightColor = parseTextColor(rightColorString, NamedTextColor.RED));
    }

    @NotNull
    private TextColor parseTextColor(String colorString, TextColor defaultColor) {
        if (colorString == null || colorString.isEmpty()) {
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
