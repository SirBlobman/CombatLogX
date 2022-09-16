package combatlogx.expansion.death.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.xseries.XMaterial;

public final class DeathEffectsConfiguration implements IConfigurable {
    private boolean requireCombatDeath;
    private final List<String> deathEffectList;

    private boolean lightningEffectOnly;
    private boolean lightningSilent;

    private XMaterial bloodItemsMaterial;
    private int bloodItemsAmount;
    private long bloodItemsStayTicks;

    public DeathEffectsConfiguration() {
        this.requireCombatDeath = true;
        this.deathEffectList = new ArrayList<>();

        this.lightningEffectOnly = true;
        this.lightningSilent = true;

        this.bloodItemsMaterial = XMaterial.RED_DYE;
        this.bloodItemsAmount = 10;
        this.bloodItemsStayTicks = 100L;
    }


    @Override
    public void load(ConfigurationSection section) {
        setRequireCombatDeath(section.getBoolean("combat-death-only", true));
        setDeathEffectList(section.getStringList("death-effect-list"));

        ConfigurationSection sectionLightning = getOrCreateSection(section, "lightning");
        setLightningEffectOnly(sectionLightning.getBoolean("effect-only", true));
        setLightningSilent(sectionLightning.getBoolean("silent", true));

        ConfigurationSection sectionBloodItems = getOrCreateSection(section, "blood-items");
        setBloodItemsAmount(sectionBloodItems.getInt("amount", 10));
        setBloodItemsStayTicks(sectionBloodItems.getLong("stay-ticks", 100L));
        loadBloodItemsMaterial(sectionBloodItems);
    }

    private void loadBloodItemsMaterial(ConfigurationSection section) {
        String bloodItemsMaterialName = section.getString("material", "RED_DYE");
        if (bloodItemsMaterialName == null) {
            bloodItemsMaterialName = "RED_DYE";
        }

        Optional<XMaterial> optionalBloodItemsMaterial = XMaterial.matchXMaterial(bloodItemsMaterialName);
        XMaterial bloodItemsMaterial = optionalBloodItemsMaterial.orElse(XMaterial.RED_DYE);
        setBloodItemsMaterial(bloodItemsMaterial);
    }

    public boolean isRequireCombatDeath() {
        return requireCombatDeath;
    }

    public void setRequireCombatDeath(boolean requireCombatDeath) {
        this.requireCombatDeath = requireCombatDeath;
    }

    public List<String> getDeathEffectList() {
        return Collections.unmodifiableList(this.deathEffectList);
    }

    public void setDeathEffectList(Collection<String> deathEffectList) {
        this.deathEffectList.clear();
        this.deathEffectList.addAll(deathEffectList);
    }

    public boolean hasEffect(String deathEffect) {
        List<String> deathEffectList = getDeathEffectList();
        return deathEffectList.contains(deathEffect);
    }

    public boolean isLightningEffectOnly() {
        return lightningEffectOnly;
    }

    public void setLightningEffectOnly(boolean lightningEffectOnly) {
        this.lightningEffectOnly = lightningEffectOnly;
    }

    public boolean isLightningSilent() {
        return lightningSilent;
    }

    public void setLightningSilent(boolean lightningSilent) {
        this.lightningSilent = lightningSilent;
    }

    public XMaterial getBloodItemsMaterial() {
        return bloodItemsMaterial;
    }

    public void setBloodItemsMaterial(XMaterial bloodItemsMaterial) {
        this.bloodItemsMaterial = bloodItemsMaterial;
    }

    public int getBloodItemsAmount() {
        return bloodItemsAmount;
    }

    public void setBloodItemsAmount(int bloodItemsAmount) {
        this.bloodItemsAmount = bloodItemsAmount;
    }

    public long getBloodItemsStayTicks() {
        return bloodItemsStayTicks;
    }

    public void setBloodItemsStayTicks(long bloodItemsStayTicks) {
        this.bloodItemsStayTicks = bloodItemsStayTicks;
    }
}
