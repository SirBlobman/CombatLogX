package com.SirBlobman.combatlogx.expansion;

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

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

public class Expansions {
	private static final File FOLDER = CombatLogX.FOLDER;
	private static final File EFOLDER = new File(FOLDER, "expansions");
	private static List<CLXExpansion> EXPANSIONS = Util.newList();
	
	/**
	 * Register your expansion with CombatLogX
	 * @param clazz The main expansion class (should implement {@link CLXExpansion})
	 * @return 
	 * {@code true} if the expansion loaded successfully
	 * {@code false} if the expansion failed to load or there was an error
	 */
	public static boolean loadExpansion(Class<?> clazz) {
		try {
			Class<?>[] interfaceClasses = clazz.getInterfaces();
			boolean loaded = false;
			for(Class<?> interfaceClass : interfaceClasses) {
				if(interfaceClass.equals(CLXExpansion.class)) {
					CLXExpansion clxe = (CLXExpansion) clazz.newInstance();
					String name = clxe.getName();
					String uname = clxe.getUnlocalizedName();
					String version = clxe.getVersion();
					
					List<String> keys = Util.newList("{name}", "{unlocalized_name}", "{version}");
					List<?> vals = Util.newList(name, uname, version);
					String format = ConfigLang.get("messages.loading expansion");
					String msg = Util.formatMessage(format, keys, vals);
					Util.print(msg);
					
					clxe.enable();
					EXPANSIONS.add(clxe);
					
					loaded = true;
					break;
				}
			}
			
			return loaded;
		} catch(Throwable ex) {
			String error = "Failed to load expansion: ";
			Util.log(error);
			ex.printStackTrace();
			return false;
		}
	}
	
	public static List<CLXExpansion> getExpansions() {
		List<CLXExpansion> copy = Util.newList(EXPANSIONS);
		return copy;
	}
	
	public static void reloadConfigs() {
		List<CLXExpansion> list = getExpansions();
		list.forEach(clxe -> clxe.onConfigReload());
	}
	
	public static CLXExpansion getExpansion(String name) {
		List<CLXExpansion> list = getExpansions();
		for(CLXExpansion clxe : list) {
			String cname = clxe.getName();
			if(cname.equals(name)) return clxe;
			
			String uname = clxe.getUnlocalizedName();
			if(uname.equals(name)) return clxe;
		}
		return null;
	}
	
	public static boolean isEnabled(String name) {
		return (getExpansion(name) != null);
	}
	
	public static void onDisable() {
		List<CLXExpansion> list = getExpansions();
		EXPANSIONS.clear();
		list.forEach(clxe -> clxe.disable());
	}
	
	public static void loadExpansions() {
		try {
			if(!EFOLDER.exists()) EFOLDER.mkdirs();
			File[] files = EFOLDER.listFiles();
			for(File file : files) {
				if(!file.isDirectory()) {
					if(isJar(file)) {
						JarFile jarFile = null;
						try {
							jarFile = loadJar(file);
							Enumeration<JarEntry> entries = jarFile.entries();
							while(entries.hasMoreElements()) {
								JarEntry je = entries.nextElement();
								if(!je.isDirectory()) {
									String entryName = getClassName(je);
									try {
										if(isClass(je)) {
											Class<?> clazz = Class.forName(entryName);
											if(loadExpansion(clazz)) break;
										}
									} catch(Throwable ex4) {}
								}
							}
						} catch(Throwable ex2) {
							String error = "Failed to install expansion from JAR '" + file + "':";
							Util.log(error);
							ex2.printStackTrace();
						} finally {
							try {
								if(jarFile != null) jarFile.close();
							} catch(Throwable ex3) {}
						}
					} else {
						String error = "Found a non-jar file at '" + file + "'. Please remove it!";
						Util.log(error);
					}
				}
			}
			
			int count = getExpansions().size();
			List<String> keys = Util.newList("{amount}", "{s}");
			List<?> vals = Util.newList(count, (count == 1) ? "" : "s");
			String format = ConfigLang.get("messages.loaded expansions");
			String msg = Util.formatMessage(format, keys, vals);
			Util.print(msg);
		} catch(Throwable ex1) {
			String error1 = "There was an extreme error loading any expansions for CombatLogX!";
			String error2 = "Please send this message to SirBlobman on SpigotMC along with your 'latest.log' file!";
			Util.print(error1, error2);
			ex1.printStackTrace();
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
		if(lastDot > lastSep) {
			int start = (lastDot + 1);
			String extension = name.substring(start).toLowerCase();
			return extension;
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
		if(classLoader instanceof URLClassLoader) {
			URLClassLoader ucl = (URLClassLoader) classLoader;
			URI uri = file.toURI();
			URL url = uri.toURL();
			for(URL urls : ucl.getURLs()) {if(urls.equals(url)) return jarFile;}
			
			Class<URLClassLoader> clazz = URLClassLoader.class;
			Method method = clazz.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(ucl, url);
			return jarFile;
		} else return null;
	}
	
	private static boolean isClass(JarEntry je) {
		String name = je.getName();
		return name.endsWith(".class");
	}
	
	private static String getClassName(JarEntry je) {
		if(isClass(je)) {
			String name = je.getName()
			  .replace(".class", "")
			  .replace("/", ".")
			  .replace(File.separator, ".");
			return name;
		} else return "";
	}
}