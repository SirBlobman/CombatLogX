package com.SirBlobman.notify.nms;

import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.notify.Notifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NMSUtil extends Util {
    public static String minecraftVersion() {
        String version = Bukkit.getVersion();
        Pattern pat = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))");
        Matcher mat = pat.matcher(version);
        if(mat.find()) return mat.group(2);
        return "";
    }
    
    public static String baseVersion() {
        String version = minecraftVersion();
        int last = version.lastIndexOf('.');
        String base = (last < 2) ? version : version.substring(0, last);
        return base;
    }
    
    public static NMSUtil getNMS() {
        String mc = minecraftVersion();
        switch(mc) {
            case "1.12.2":
            case "1.12.1":
            case "1.12":
                return new NMS1_12_R1();
            case "1.11.2":
            case "1.11.1":
            case "1.11":
                return new NMS1_11_R1();
            case "1.10.2":
            case "1.10.1":
            case "1.10":
                return new NMS1_10_R1();
            case "1.9.4":
                return new NMS1_9_R2();
            case "1.9.2":
            case "1.9.1":
            case "1.9":
                return new NMS1_9_R1();
            case "1.8.9":
            case "1.8.8":
            case "1.8.7":
            case "1.8.6":
            case "1.8.5":
            case "1.8.4":
                return new NMS1_8_R3();
            case "1.8.3":
                return new NMS1_8_R2();
            case "1.8.2":
            case "1.8.1":
            case "1.8":
                return new NMS1_8_R1();
            default:
                Notifier.log(
                    "NMS for '" + mc + "' is not supported!",
                    "This means that some scoreboard features and the action bar will not work!"
                );
                return null;
        }
    }
    
    public static String json(String o) {
        String p1 = "{\"text\": \"";
        String p2 = "\"}";
        String json = p1 + o + p2;
        return json;
    }
    
    public abstract void action(Player p, String msg);
}