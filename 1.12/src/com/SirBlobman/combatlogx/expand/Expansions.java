package com.SirBlobman.combatlogx.expand;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.WordUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Expansions {
	public static final File FOLDER = new File(CombatLogX.folder, "expansions");
	private static List<CLXExpansion> expansionList = Util.newList();
	public static void load() {
		if(!FOLDER.exists()) FOLDER.mkdirs();
		File[] ff = FOLDER.listFiles();
		for(File f : ff) {
			boolean b = isJAR(f);
			if(b) {
				JarFile jar = null;
				try {
					loadJAR(f);
					jar = new JarFile(f);
					Enumeration<JarEntry> jes = jar.entries();
					while(jes.hasMoreElements()) {
						JarEntry je = jes.nextElement();
						if(!je.isDirectory()) {
							String jname = className(je);
							try {
								boolean b1 = isClass(je);
								if(b1) {
									Class<?> clazz = Class.forName(jname);
									Class<?>[] interfs = clazz.getInterfaces();
									for(Class<?> inter : interfs) {
										if(inter.equals(CLXExpansion.class)) {
											CLXExpansion ce = (CLXExpansion) clazz.newInstance();
											String name = ce.getName();
											String version = ce.getVersion();
											String msg = Util.format("Loading expansion '%1s v%2s'", name, version);
											Util.print(msg);
											ce.enable();
											expansionList.add(ce);
											break;
										} else continue;
									}
								}
							} catch(Throwable ex) {
								String error = "Failed to load class '" + jname + "'";
								Util.print(error);
								ex.printStackTrace();
							}
						}
					}
				} catch(Throwable ex) {
					String error = "Failed to install expansion from jarfile '" + f + "'";
					Util.print(error);
					ex.printStackTrace();
				} finally {
					try {if(jar != null) jar.close();}
					catch(Throwable ex) {}
				}
			} else {
				String error = "Found non-jar file at '" + f + "'. Please remove it!";
				Util.print(error);
			}
		}
		
		int count = expansionsAmount();
		String msg = WordUtil.withAmount("Loaded %1s expansion", count);
		Util.print(msg);
	}
	
	private static String fileExtension(File f) {
		String name = f.getName();
		int i = name.lastIndexOf('.');
		int s1 = name.lastIndexOf('/'), s2 = name.lastIndexOf('\\');
		int s = Math.max(s1, s2);
		if(i > s) {
			int j = (i + 1);
			String ex = name.substring(j);
			String l = ex.toLowerCase();
			return l;
		} else return "";
	}
	
	private static boolean isJAR(File f) {
		String ext = fileExtension(f);
		if(ext.equals("jar")) return true;
		else return false;
	}
	
	private static synchronized void loadJAR(File f) {
		try {
			URLClassLoader ucl = (URLClassLoader) CombatLogX.class.getClassLoader();
			URI uri = f.toURI();
			URL url = uri.toURL();
			for(URL it : Util.newList(ucl.getURLs())) {if(it.equals(url)) return;}
			Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
			m.setAccessible(true);
			m.invoke(ucl, url);
		} catch(Throwable ex) {
			String error = "Failed to load JAR file '" + f + "'";
			Util.print(error);
		}
	}
	
	private static boolean isClass(JarEntry je) {
		String name = je.getName();
		boolean b = name.endsWith(".class");
		return b;
	}
	
	private static String className(JarEntry je) {
		if(isClass(je)) {
			String name = je.getName();
			String r1 = name.replace(".class", "");
			String r2 = r1.replace("/", ".");
			String r3 = r2.replace(File.separator, ".");
			return r3;
		} else return "";
	}
	
	public static List<CLXExpansion> getExpansions() {return expansionList;}
	public static int expansionsAmount() {return getExpansions().size();}
}