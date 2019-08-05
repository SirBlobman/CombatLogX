package com.SirBlobman.combatlogx.expansion;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Expansions {
	private static final File FOLDER = CombatLogX.FOLDER;
	private static final File EFOLDER = new File(FOLDER, "expansions");
	private static List<CLXExpansion> EXPANSIONS = Util.newList();

	/**
	 * Register your expansion with CombatLogX
	 *
	 * @param clazz The main expansion class (should implement {@link CLXExpansion})
	 * @return {@code true} if the expansion loaded successfully
	 * {@code false} if the expansion failed to load or there was an error
	 */
	private static boolean loadExpansion(Class<?> clazz) {
		try {
			Class<?> expansionClass = CLXExpansion.class;
			if(!expansionClass.isAssignableFrom(clazz)) return false;
			
			CLXExpansion expansion = (CLXExpansion) clazz.getDeclaredConstructor().newInstance();
			String expansionName = expansion.getName();
			String unlocalizedName = expansion.getUnlocalizedName();
			String expansionVersion = expansion.getVersion();
			
			List<String> keyList = Util.newList("{name}", "{unlocalized_name}", "{version}");
			List<String> valList = Util.newList(expansionName, unlocalizedName, expansionVersion);
			String message = Util.formatMessage(ConfigLang.get("messages.loading expansion"), keyList, valList);
			Util.print(message);

			EXPANSIONS.add(expansion);
			if(expansion.isPreloaded()) expansion.load();
			
			return true;
		} catch (Throwable ex) {
			String error = "Failed to load expansion: ";
			Util.log(error);
			ex.printStackTrace();
			return false;
		}
	}

	public static void enableExpansions() {
		for(CLXExpansion clxe : getExpansions()) {
			String name = clxe.getName();
			String uname = clxe.getUnlocalizedName();
			String version = clxe.getVersion();

			List<String> keys = Util.newList("{name}", "{unlocalized_name}", "{version}");
			List<?> vals = Util.newList(name, uname, version);
			String format = ConfigLang.get("messages.enabling expansion");
			String msg = Util.formatMessage(format, keys, vals);
			Util.print(msg);
			try {
				clxe.enable();
				Util.print(" ");
			} catch (Throwable ex) {
				ex.printStackTrace();
				continue;
			}
		}


		int count = getExpansions().size();
		List<String> keyList = Util.newList("{amount}", "{s}");
		List<?> valueList = Util.newList(count, (count == 1 ? "" : "s"));
		String message = Util.formatMessage(ConfigLang.get("messages.enabled expansions"), keyList, valueList);
		Util.print(message);

	}

	public static List<CLXExpansion> getExpansions() {
		return Util.newList(EXPANSIONS);
	}

	public static boolean isEnabled(String unlocalizedName) {
		List<CLXExpansion> enabled = getExpansions();
		List<String> expansionNames = enabled.stream().map(CLXExpansion::getUnlocalizedName).collect(Collectors.toList());
		return expansionNames.contains(unlocalizedName);
	}

	public static void reloadConfigs() {
		List<CLXExpansion> list = getExpansions();
		list.forEach(CLXExpansion::onConfigReload);
	}

	public static void onDisable() {
		List<CLXExpansion> list = getExpansions();
		EXPANSIONS.clear();
		list.forEach(CLXExpansion::disable);
	}

	public static void loadExpansions() {
		try {
			if(!EFOLDER.exists()) {
				Util.debug("Expansion folder didn't exist, creating...");
				EFOLDER.mkdirs();
			}

			File[] files = EFOLDER.listFiles();
			if(files == null) files = new File[0];

			for(File file : files) {
				Util.debug("Checking file '" + file.getName() + "' for expansions...");

				if(file.isDirectory()) {
					Util.debug("File is a folder, ignoring.");
					continue;
				}

				if(!isJar(file)) {
					Util.debug("File is not a jar, ignoring.");
					continue;
				}

				JarFile jarFile = loadJar(file);
				if(jarFile == null) {
					Util.debug("File is an invalid jar, ignoring.");
					continue;
				}

				Enumeration<JarEntry> jarEntries = jarFile.entries();
				a: while(jarEntries != null && jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();
					Util.debug("Checking jar entry '" + jarEntry.getName() + "'.");

					if(jarEntry.isDirectory()) {
						Util.debug("Jar Entry is a folder, ignoring.");
						continue a;
					}

					String entryName = getClassName(jarEntry);
					if(!isClass(jarEntry)) {
						Util.debug("Jar Entry is not a class file, ignoring.");
						continue a;
					}

					Class<?> entryClass = null;
					try {
						entryClass = Class.forName(entryName);
					} catch(Throwable ex) {
						Util.debug("An error occurred while loading an expansion: " + ex.getMessage());
						continue a;
					}

					if(loadExpansion(entryClass)) {
						Util.debug("Successfully loaded expansion.");
						break a;
					} else {
						Util.debug("Failed to load expansion.");
						continue a;
					}
				}
			}

			int count = getExpansions().size();
			List<String> keys = Util.newList("{amount}", "{s}");
			List<?> vals = Util.newList(count, (count == 1) ? "" : "s");
			String format = ConfigLang.get("messages.loaded expansions");
			String msg = Util.formatMessage(format, keys, vals);
			Util.print(msg);
		} catch(ReflectiveOperationException | IOException ex) {
			Util.log("An error occurred while trying to load expansions:");
			ex.printStackTrace();
		}
	}

	public static void unloadExpansion(CLXExpansion exp) {
		exp.disable();
		EXPANSIONS.remove(exp);
	}

	private static String getFileExtension(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf(".");
		int lastSep = Math.max(name.lastIndexOf("/"), name.lastIndexOf("\\"));
		if (lastDot > lastSep) {
			int start = (lastDot + 1);
			return name.substring(start).toLowerCase();
		} else return "";
	}

	private static boolean isJar(File file) {
		String ext = getFileExtension(file);
		return ext.equals("jar");
	}

	@SuppressWarnings("resource")
	private static synchronized JarFile loadJar(File file) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		JarFile jarFile = new JarFile(file);
		ClassLoader classLoader = CombatLogX.CLASS_LOADER;
		if(!(classLoader instanceof URLClassLoader)) return null;

		URLClassLoader ucl = (URLClassLoader) classLoader;
		URI uri = file.toURI();
		URL url = uri.toURL();
		for (URL urls : ucl.getURLs()) {
			if (urls.equals(url)) return jarFile;
		}

		Class<URLClassLoader> clazz = URLClassLoader.class;
		Method method = clazz.getDeclaredMethod("addURL", URL.class);
		method.setAccessible(true);
		method.invoke(ucl, url);
		return jarFile;
	}

	private static boolean isClass(JarEntry je) {
		String name = je.getName();
		return name.endsWith(".class");
	}

	private static String getClassName(JarEntry je) {
		if(!isClass(je)) return "";

		String name = je.getName();
		return name.replace(".class", "").replace("/", ".").replace(File.separator, ".");
	}
}