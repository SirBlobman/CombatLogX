package com.github.sirblobman.combatlogx.api.expansion;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.combatlogx.api.ICombatLogX;

class ExpansionClassLoader extends URLClassLoader {
    private final ExpansionManager manager;
    private final Map<String, Class<?>> classes;
    private Expansion expansion;

    ExpansionClassLoader(@NotNull ExpansionManager manager, @NotNull YamlConfiguration description,
                         @NotNull File path, @NotNull ClassLoader parent)
            throws MalformedURLException {
        super(new URL[]{path.toURI().toURL()}, parent);
        this.manager = manager;
        this.classes = new HashMap<>();

        registerExpansion(description, path);
    }

    @Override
    protected @Nullable Class<?> findClass(String name) {
        return findClass(name, true);
    }

    public @Nullable Class<?> findClass(String name, boolean checkGlobal) {
        Class<?> result = classes.get(name);
        if (result == null) {
            if (checkGlobal) {
                result = this.manager.getClassByName(name);
            }

            if (result == null) {
                try {
                    result = super.findClass(name);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // Do nothing.
                } catch (UnsupportedClassVersionError e) {
                    ICombatLogX plugin = this.manager.getPlugin();
                    Logger logger = plugin.getLogger();
                    logger.warning("Could not load class with name=" + name + ", global=" + checkGlobal
                            + " because an error occurred:");
                    logger.warning(e.getMessage());
                }

                if (result != null) {
                    this.manager.setClass(name, result);
                }
            }
            classes.put(name, result);
        }
        return result;
    }

    public @Nullable Expansion getExpansion() {
        return this.expansion;
    }

    public @NotNull Set<String> getClasses() {
        return this.classes.keySet();
    }

    private void registerExpansion(@NotNull YamlConfiguration description, @NotNull File path) {
        Class<?> mainClass;
        try {
            String mainClassName = description.getString("main");
            if (mainClassName == null) {
                throw new IllegalStateException("Could not find `main` in expansion.yml");
            }

            mainClass = Class.forName(mainClassName, true, this);
        } catch (ReflectiveOperationException ex) {
            String newError = ("Could not load '" + path.getName() + "' from folder '" + path.getParent() + "'");
            throw new IllegalStateException(newError, ex);
        }

        Class<? extends Expansion> expansionClass;
        try {
            expansionClass = mainClass.asSubclass(Expansion.class);
        } catch (ClassCastException ex) {
            String newError = ("Main class is not an instance of 'Expansion'");
            throw new IllegalStateException(newError, ex);
        }

        try {
            ICombatLogX plugin = this.manager.getPlugin();
            Constructor<? extends Expansion> constructor = expansionClass.getDeclaredConstructor(ICombatLogX.class);
            this.expansion = constructor.newInstance(plugin);

            ExpansionDescription expansionDescription = createDescription(description);
            this.expansion.setDescription(expansionDescription);
        } catch (ReflectiveOperationException ex) {
            String pathName = path.getName();
            String pathParent = path.getParent();
            String errorMessageFormat = "Could not load '%s' from folder '%s'.";

            String errorMessage = String.format(Locale.US, errorMessageFormat, pathName, pathParent);
            throw new IllegalStateException(errorMessage, ex);
        }
    }

    private ExpansionDescription createDescription(@NotNull YamlConfiguration configuration)
            throws IllegalStateException {
        String mainClass = configuration.getString("main");
        if (mainClass == null) {
            throw new IllegalStateException("'main' is required in expansion.yml");
        }

        String name = configuration.getString("name");
        if (name == null) {
            throw new IllegalStateException("'name' is required in expansion.yml");
        }

        String version = configuration.getString("version");
        if (version == null) {
            throw new IllegalStateException("'version' is required in expansion.yml");
        }

        String prefix = configuration.getString("display-name");

        if (prefix == null) {
            prefix = configuration.getString("prefix");
        }

        if (prefix == null) {
            prefix = name;
        }

        String description = configuration.getString("description", "");
        String website = configuration.getString("website", null);

        List<String> authorList = configuration.getStringList("authors");
        if (authorList.isEmpty()) {
            authorList = new ArrayList<>();
        }

        String author = configuration.getString("author", null);
        if (author != null) {
            authorList.add(author);
        }

        List<String> pluginDependList = configuration.getStringList("plugin-depend");
        List<String> pluginSoftDependList = configuration.getStringList("plugin-soft-depend");
        List<String> expansionDependList = configuration.getStringList("expansion-depend");
        List<String> expansionSoftDependList = configuration.getStringList("expansion-soft-depend");
        boolean lateLoad = configuration.getBoolean("late-load", false);

        ExpansionDescriptionBuilder builder = new ExpansionDescriptionBuilder(mainClass, name, version)
                .withPrefix(prefix)
                .withDescription(description)
                .withWebsite(website)
                .withAuthors(authorList)
                .withPluginDependencies(pluginDependList)
                .withPluginSoftDependencies(pluginSoftDependList)
                .withExpansionDependencies(expansionDependList)
                .withExpansionSoftDependencies(expansionSoftDependList)
                .withLateLoad(lateLoad);
        return builder.build();
    }
}
