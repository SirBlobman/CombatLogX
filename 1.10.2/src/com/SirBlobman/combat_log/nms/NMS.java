package com.SirBlobman.combat_log.nms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.SirBlobman.combat_log.nms.action.Action;
import com.SirBlobman.combat_log.nms.action.Action1_10_R1;
import com.SirBlobman.combat_log.nms.action.Action1_11_R1;
import com.SirBlobman.combat_log.nms.action.Action1_8_R1;
import com.SirBlobman.combat_log.nms.action.Action1_8_R2;
import com.SirBlobman.combat_log.nms.action.Action1_8_R3;
import com.SirBlobman.combat_log.nms.action.Action1_9_R1;
import com.SirBlobman.combat_log.nms.action.Action1_9_R2;

public class NMS
{
	private static String mcVersion()
	{
		String version = Bukkit.getVersion();
		Pattern pat = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))");
		Matcher mat = pat.matcher(version);
		if(mat.find()) return mat.group(2);
		return "";
	}
	
	private static String nms()
	{
		String version = mcVersion();
		switch(version)
		{
		case "1.7.2": return "v1_7_R1";
		case "1.7.3": return "v1_7_R1";
		case "1.7.4": return "v1_7_R1";
		case "1.7.5": return "v1_7_R2";
		case "1.7.6": return "v1_7_R2";
		case "1.7.7": return "v1_7_R2";
		case "1.7.8": return "v1_7_R3";
		case "1.7.9": return "v1_7_R3";
		case "1.7.10": return "v1_7_R4";
		case "1.8": return "v1_8_R1";
		case "1.8.1": return "v1_8_R1";
		case "1.8.2": return "v1_8_R1";
		case "1.8.3": return "v1_8_R2";
		case "1.8.4": return "v1_8_R3";
		case "1.8.5": return "v1_8_R3";
		case "1.8.6": return "v1_8_R3";
		case "1.8.7": return "v1_8_R3";
		case "1.8.8": return "v1_8_R3";
		case "1.8.9": return "v1_8_R3";
		case "1.9": return "v1_9_R1";
		case "1.9.1": return "v1_9_R1";
		case "1.9.2": return "v1_9_R1";
		case "1.9.4": return "v1_9_R2";
		case "1.10": return "v1_10_R1";
		case "1.10.1": return "v1_10_R1";
		case "1.10.2": return "v1_10_R1";
		case "1.11": return "v1_11_R1";
		default: return "[failed to get NMS version]";
		}
	}
	
	public static Action getAction()
	{
		String nms = nms();
		switch(nms)
		{
		case "v1_8_R1": return new Action1_8_R1();
		case "v1_8_R2": return new Action1_8_R2();
		case "v1_8_R3": return new Action1_8_R3();
		case "v1_9_R1": return new Action1_9_R1();
		case "v1_9_R2": return new Action1_9_R2();
		case "v1_10_R1": return new Action1_10_R1();
		case "v1_11_R1": return new Action1_11_R1();
		default: {System.out.println("Action Bar is not supported in " + nms); return null;}
		}
	}
}