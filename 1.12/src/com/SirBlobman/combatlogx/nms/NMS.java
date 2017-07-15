package com.SirBlobman.combatlogx.nms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.SirBlobman.combatlogx.nms.action.*;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;

public class NMS {
	private static String version() {
		String version = Bukkit.getVersion();
		Pattern pat = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))");
		Matcher mat = pat.matcher(version);
		if(mat.find()) return mat.group(2);
		return "";
	}
	
	public static String nms() {
		String version = version();
		switch(version) {
		case "1.7.2":
		case "1.7.3":
		case "1.7.4":
		case "1.7.5":
		case "1.7.6":
		case "1.7.7":
		case "1.7.8":
		case "1.7.9":
		case "1.7.10":
			return "v1_7";
		case "1.8":
		case "1.8.1":
		case "1.8.2":
			return "v1_8_R1";
		case "1.8.3":
			return "v1_8_R2";
		case "1.8.4":
		case "1.8.5":
		case "1.8.6":
		case "1.8.7":
		case "1.8.8":
		case "1.8.9":
			return "v1_8_R3";
		case "1.9":
		case "1.9.1":
		case "1.9.2":
			return "v1_9_R1";
		case "1.9.4":
			return "v1_9_R2";
		case "1.10":
		case "1.10.1":
		case "1.10.2":
			return "v1_10_R1";
		case "1.11":
		case "1.11.1":
		case "1.11.2":
			return "v1_11_R1";
		case "1.12":
			return "v1_12_R1";
		default:
			return "[Failed to get NMS]";
		}
	}
	
	public static Action action() {
		String nms = nms();
		switch(nms) {
		case "v1_8_R1": return new Action1_8_R1();
		case "v1_8_R2": return new Action1_8_R2();
		case "v1_8_R3": return new Action1_8_R3();
		case "v1_9_R1": return new Action1_9_R1();
		case "v1_9_R2": return new Action1_9_R2();
		case "v1_10_R1": return new Action1_10_R1();
		case "v1_11_R1": return new Action1_11_R1();
		case "v1_12_R1": return new Action1_12_R1();
		default:
			Util.print("ActionBar is not supported in " + nms);
			return null;
		}
	}
}