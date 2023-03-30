package combatlogx.expansion.boss.bar;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.adventure.bossbar.BossBar;
import com.github.sirblobman.api.shaded.adventure.text.format.NamedTextColor;
import com.github.sirblobman.api.shaded.adventure.text.format.TextColor;

import org.jetbrains.annotations.NotNull;

public final class BossBarConfiguration implements IConfigurable {
    private boolean enabled;
    private String bossBarColorName;
    private String bossBarStyleName;

    private long scale;
    private String leftColorString;
    private String rightColorString;
    private String leftSymbol;
    private String rightSymbol;

    private transient BossBar.Color bossBarColor;
    private transient BossBar.Overlay bossBarStyle;
    private transient TextColor leftColor;
    private transient TextColor rightColor;

    public BossBarConfiguration() {
        setEnabled(true);
        setBossBarColorName("YELLOW");
        setBossBarStyleName("PROGRESS");

        setScale(15L);
        setLeftColorString("GREEN");
        setRightColorString("RED");
        setLeftSymbol("|");
        setRightSymbol("|");
    }

    @Override
    public void load(ConfigurationSection config) {
        setEnabled(config.getBoolean("enabled", true));
        setBossBarColorName(config.getString("bar-color", "YELLOW"));
        setBossBarStyleName(config.getString("bar-style", "PROGRESS"));

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

    public String getBossBarColorName() {
        return bossBarColorName;
    }

    public void setBossBarColorName(String bossBarColorName) {
        this.bossBarColorName = bossBarColorName;
        this.bossBarColor = null;
    }

    public String getBossBarStyleName() {
        return bossBarStyleName;
    }

    public void setBossBarStyleName(String bossBarStyleName) {
        this.bossBarStyleName = bossBarStyleName;
        this.bossBarStyle = null;
    }

    @NotNull
    public BossBar.Color getBossBarColor() {
        if (this.bossBarColor != null) {
            return this.bossBarColor;
        }

        String bossBarColorName = getBossBarColorName();
        return (this.bossBarColor = parseBossBarColor(bossBarColorName));
    }

    @NotNull
    public BossBar.Overlay getBossBarStyle() {
        if (this.bossBarStyle != null) {
            return this.bossBarStyle;
        }

        String bossBarStyleName = getBossBarStyleName();
        return (this.bossBarStyle = parseBossBarStyle(bossBarStyleName));
    }

    private BossBar.Color parseBossBarColor(String name) {
        if (name == null || name.isEmpty()) {
            return BossBar.Color.YELLOW;
        }

        try {
            return BossBar.Color.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return BossBar.Color.YELLOW;
        }
    }

    private BossBar.Overlay parseBossBarStyle(String name) {
        if (name == null || name.isEmpty()) {
            return BossBar.Overlay.PROGRESS;
        }

        try {
            return BossBar.Overlay.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return BossBar.Overlay.PROGRESS;
        }
    }
}
