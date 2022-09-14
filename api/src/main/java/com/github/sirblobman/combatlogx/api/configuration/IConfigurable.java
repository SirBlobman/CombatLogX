package com.github.sirblobman.combatlogx.api.configuration;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.utility.Validate;

public interface IConfigurable {
    /**
     * Load the values from a configuration.
     * @param section The configuration that contains the values.
     */
    void load(ConfigurationSection section);

    /**
     * Get or create a configuration section.
     * @param parent The parent section.
     * @param path The path to check.
     * @return The original section at the path, or a new one if a section did not exist.
     */
    default ConfigurationSection getOrCreateSection(ConfigurationSection parent, String path) {
        Validate.notNull(parent, "parent section must not be null!");
        Validate.notEmpty(path, "path must not be empty!");

        ConfigurationSection oldSection = parent.getConfigurationSection(path);
        if (oldSection != null) {
            return oldSection;
        }

        return parent.createSection(path);
    }
}
