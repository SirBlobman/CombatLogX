package com.github.sirblobman.combatlogx.api.expansion;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.combatlogx.api.ICombatLogX;

class ExpansionClassLoader extends URLClassLoader {
    private final ExpansionManager manager;
    private final Map<String, Class<?>> classes;
    private Expansion expansion;

    ExpansionClassLoader(ExpansionManager manager, YamlConfiguration description, File path, ClassLoader parent)
            throws MalformedURLException {
        super(new URL[]{path.toURI().toURL()}, parent);
        this.manager = manager;
        this.classes = new HashMap<>();

        registerExpansion(description, path);
    }

    @Override
    protected Class<?> findClass(String name) {
        return findClass(name, true);
    }

    public Class<?> findClass(String name, boolean checkGlobal) {
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

    public Expansion getExpansion() {
        return this.expansion;
    }

    public Set<String> getClasses() {
        return this.classes.keySet();
    }

    private void registerExpansion(YamlConfiguration description, File path) {
        Class<?> mainClass;
        try {
            String mainClassName = description.getString("main");
            if (mainClassName == null) throw new IllegalStateException("Could not find `main` in expansion.yml");

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
            Constructor<? extends Expansion> declaredConstructor = expansionClass.getDeclaredConstructor(ICombatLogX.class);
            this.expansion = declaredConstructor.newInstance(this.manager.getPlugin());

            ExpansionDescription expansionDescription = createDescription(description);
            this.expansion.setDescription(expansionDescription);
        } catch (ReflectiveOperationException ex) {
            String newError = ("Could not load '" + path.getName() + "' from folder '" + path.getParent() + "'");
            throw new IllegalStateException(newError, ex);
        }
    }

    private ExpansionDescription createDescription(YamlConfiguration configuration) throws IllegalStateException {
        String mainClassName = configuration.getString("main");
        if (mainClassName == null) throw new IllegalStateException("'main' is required in expansion.yml");

        String unlocalizedName = configuration.getString("name");
        if (unlocalizedName == null) throw new IllegalStateException("'name' is required in expansion.yml");

        String version = configuration.getString("version");
        if (version == null) throw new IllegalStateException("'version' is required in expansion.yml");

        ExpansionDescriptionBuilder builder = new ExpansionDescriptionBuilder(mainClassName, unlocalizedName, version);
        String displayName = configuration.getString("display-name", null);
        if (displayName == null) displayName = configuration.getString("prefix", null);
        if (displayName == null) displayName = unlocalizedName;
        builder.withDisplayName(displayName);

        String description = configuration.getString("description", null);
        if (description != null) builder.withDescription(description);

        String website = configuration.getString("website", null);
        if (website != null) builder.withWebsite(website);

        List<String> authorList = configuration.getStringList("authors");
        if (authorList.isEmpty()) authorList = new ArrayList<>();

        String author = configuration.getString("author", null);
        if (author != null) authorList.add(author);
        builder.withAuthors(authorList);

        List<String> pluginDependList = configuration.getStringList("plugin-depend");
        builder.withPluginDependencies(pluginDependList);

        List<String> pluginSoftDependList = configuration.getStringList("plugin-soft-depend");
        builder.withPluginSoftDependencies(pluginSoftDependList);

        List<String> expansionDependList = configuration.getStringList("expansion-depend");
        builder.withExpansionDependencies(expansionDependList);

        List<String> expansionSoftDependList = configuration.getStringList("expansion-soft-depend");
        builder.withExpansionSoftDependencies(expansionSoftDependList);

        boolean lateLoad = configuration.getBoolean("late-load", false);
        builder.withLateLoad(lateLoad);

        return builder.build();
    }
}
