package com.SirBlobman.combat_log.utility;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.SirBlobman.combat_log.Config;

public class Util
{
	public static final String prefix = Config.option("messages.prefix") + " ";
	public static final String prefix2 = uncolor(prefix);
	
	/**
	 * Add some color to your messages
	 * @param o Message with &amp; color codes
	 * @return <b>Color Coded Message</b><br/>
	 * &0 = <span style="color: #000000;">Black</span><br/>
	 * &1 = <span style="color: #0000AA;">Dark Blue</span><br/>
	 * &2 = <span style="color: #00AA00;">Dark Green</span><br/>
	 * &3 = <span style="color: #00AAAA;">Dark Aqua</span><br/>
	 * &4 = <span style="color: #AA0000;">Dark Red</span><br/>
	 * &5 = <span style="color: #AA00AA;">Dark Purple</span><br/>
	 * &6 = <span style="color: #FFAA00;">Gold</span><br/>
	 * &7 = <span style="color: #AAAAAA;">Gray</span><br/>
	 * &8 = <span style="color: #555555;">Dark Gray</span><br/>
	 * &9 = <span style="color: #5555FF;">Blue</span><br/>
	 * &a = <span style="color: #55FF55;">Green</span><br/>
	 * &b = <span style="color: #55FFFF;">Aqua</span><br/>
	 * &c = <span style="color: #FF5555;">Red</span><br/>
	 * &d = <span style="color: #FF55FF;">Light Purple</span><br/>
	 * &e = <span style="color: #FFFF55;">Yellow</span><br/>
	 * &f = <span style="color: #FFFFFF;">White</span><br/>
	 * &k = <blink>Magic</blink><br/>
	 * &l = <b>Bold</b><br/>
	 * &m = <s>Strike</s><br/>
	 * &n = <u>Underline</u><br/>
	 * &o = <i>Italics</i><br/>
	 * &r = Reset<br/>
	 */
	public static String color(String o) {
		String c = ChatColor.translateAlternateColorCodes('&', o);
		return c;
	}
	
	public static String uncolor(String c) {
		String u = ChatColor.stripColor(c);
		return u;
	}
	
	public static void print(String msg) {
		PrintStream ps = System.out;
		String prt = prefix2 + msg;
		ps.println(prt);
	}
	
	public static <T> List<T> newList() {
		ArrayList<T> list = new ArrayList<T>();
		return list;
	}
}