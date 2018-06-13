package com.olivolja3.worldguard;

public class utils {
	
	protected static String getMaterial(String material) {
		String[] m;
		if(material.contains(":")) {
			m = material.split(":");
			return m[0];
		} else {
			return material;
		}
	}
	
	protected static byte getData(String material) {
		String[] m;
		if(material.contains(":")) {
			m = material.split(":");
			return (byte) Integer.parseInt(m[1]);
		} else {
			return 0;
		}
	}
	
}
