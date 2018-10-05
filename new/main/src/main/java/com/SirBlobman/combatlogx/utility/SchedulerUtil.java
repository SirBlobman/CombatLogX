package com.SirBlobman.combatlogx.utility;

import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil extends Util {
	public static BukkitTask runLater(long delay, Runnable run) {
		BukkitTask bt = BS.runTaskLater(PLUGIN, run, delay);
		return bt;
	}
	
	public static BukkitTask runTimer(long delay, long period, Runnable run) {
		BukkitTask bt = BS.runTaskTimer(PLUGIN, run, delay, period);
		return bt;
	}

	public static BukkitTask runNowAsync(Runnable run) {
		BukkitTask bt = BS.runTaskAsynchronously(PLUGIN, run);
		return bt;
	}
	
	public static int runSync(Runnable run) {
	    int task = BS.scheduleSyncDelayedTask(PLUGIN, run);
	    return task;
	}
}