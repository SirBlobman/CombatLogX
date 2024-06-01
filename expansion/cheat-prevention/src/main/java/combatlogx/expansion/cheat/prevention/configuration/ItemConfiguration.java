package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.configuration.ConfigurationSection;

public final class ItemConfiguration implements IItemConfiguration {
    private boolean preventDrop;
    private boolean preventPickup;
    private boolean preventElytra;
    private boolean forcePreventElytra;
    private boolean elytraRetag;
    private boolean preventTotem;
    private boolean preventRiptide;
    private boolean riptideRetag;

    public ItemConfiguration() {
        this.preventDrop = true;
        this.preventPickup = true;
        this.preventElytra = true;
        this.forcePreventElytra = false;
        this.elytraRetag = false;
        this.preventTotem = false;
        this.preventRiptide = false;
        this.riptideRetag = false;
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventDrop(config.getBoolean("prevent-drop", true));
        setPreventPickup(config.getBoolean("prevent-pickup", true));
        setPreventElytra(config.getBoolean("prevent-elytra", true));
        setForcePreventElytra(config.getBoolean("force-prevent-elytra", true));
        setElytraRetag(config.getBoolean("elytra-retag", false));
        setPreventTotem(config.getBoolean("prevent-totem", false));
        setPreventRiptide(config.getBoolean("prevent-riptide", false));
        setRiptideRetag(config.getBoolean("riptide-retag", false));
    }

    @Override
    public boolean isPreventDrop() {
        return this.preventDrop;
    }

    public void setPreventDrop(boolean preventDrop) {
        this.preventDrop = preventDrop;
    }

    @Override
    public boolean isPreventPickup() {
        return this.preventPickup;
    }

    public void setPreventPickup(boolean preventPickup) {
        this.preventPickup = preventPickup;
    }

    @Override
    public boolean isPreventElytra() {
        return this.preventElytra;
    }

    public void setPreventElytra(boolean preventElytra) {
        this.preventElytra = preventElytra;
    }

    @Override
    public boolean isForcePreventElytra() {
        return this.forcePreventElytra;
    }

    public void setForcePreventElytra(boolean forcePreventElytra) {
        this.forcePreventElytra = forcePreventElytra;
    }

    @Override
    public boolean isElytraRetag() {
        return this.elytraRetag;
    }

    public void setElytraRetag(boolean elytraRetag) {
        this.elytraRetag = elytraRetag;
    }

    @Override
    public boolean isPreventTotem() {
        return this.preventTotem;
    }

    public void setPreventTotem(boolean preventTotem) {
        this.preventTotem = preventTotem;
    }

    @Override
    public boolean isPreventRiptide() {
        return this.preventRiptide;
    }

    public void setPreventRiptide(boolean preventRiptide) {
        this.preventRiptide = preventRiptide;
    }

    @Override
    public boolean isRiptideRetag() {
        return this.riptideRetag;
    }

    public void setRiptideRetag(boolean riptideRetag) {
        this.riptideRetag = riptideRetag;
    }
}
