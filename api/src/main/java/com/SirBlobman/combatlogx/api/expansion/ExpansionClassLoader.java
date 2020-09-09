package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.api.ICombatLogX;

class ExpansionClassLoader extends URLClassLoader {
    private Expansion expansion;
    private final ExpansionManager manager;
    private final Map<String, Class<?>> classes;
    ExpansionClassLoader(ExpansionManager manager, YamlConfiguration description, File path, ClassLoader parent) throws MalformedURLException {
        super(new URL[] {path.toURI().toURL()}, parent);
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
            if(mainClassName == null) throw new IllegalStateException("Could not find `main` in expansion.yml");

            mainClass = Class.forName(mainClassName, true, this);
        } catch(ReflectiveOperationException ex) {
            String newError = ("Could not load '" + path.getName() + "' from folder '" + path.getParent() + "'");
            throw new IllegalStateException(newError, ex);
        }

        Class<? extends Expansion> expansionClass;
        try {
            expansionClass = mainClass.asSubclass(Expansion.class);
        } catch(ClassCastException ex) {
            String newError = ("Main class is not an instance of 'Expansion'");
            throw new IllegalStateException(newError, ex);
        }

        try {
            Constructor<? extends Expansion> declaredConstructor = expansionClass.getDeclaredConstructor(ICombatLogX.class);
            this.expansion = declaredConstructor.newInstance(this.manager.getPlugin());

            ExpansionDescription expansionDescription = createDescription(description);
            this.expansion.setDescription(expansionDescription);
        } catch(ReflectiveOperationException ex) {
            String newError = ("Could not load '" + path.getName() + "' from folder '" + path.getParent() + "'");
            throw new IllegalStateException(newError, ex);
        }
    }

    private ExpansionDescription createDescription(YamlConfiguration config) throws IllegalStateException {
        String mainClassName = config.getString("main");
        if(mainClassName == null) throw new IllegalStateException("'main' is required in expansion.yml");

        String unlocalizedName = config.getString("name");
        if(unlocalizedName == null) throw new IllegalStateException("'name' is required in expansion.yml");

        String version = config.getString("version");
        if(version == null) throw new IllegalStateException("'version' is required in expansion.yml");

        String displayName = config.getString("display-name", null);
        if(displayName == null) displayName = config.getString("prefix", null);
        if(displayName == null) displayName = unlocalizedName;

        String description = config.getString("description", null);
        List<String> authorList = config.getStringList("authors");

        String author = config.getString("author", null);
        if(author != null) authorList.add(author);

        return new ExpansionDescription(mainClassName, unlocalizedName, displayName, description, version, authorList);
    }
}