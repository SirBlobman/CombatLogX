package com.SirBlobman.combatlogx.utility;

public class SchedulerUtil extends Util {
	public static void runLater(long delay, Runnable run) {
		BS.runTaskLater(PLUGIN, run, delay);
	}
	
	public static void runTimer(long delay, long period, Runnable run) {
		BS.runTaskTimer(PLUGIN, run, delay, period);
	}

	public static void runNowAsync(Runnable run) {
		BS.runTaskAsynchronously(PLUGIN, run);
	}
	
	public static void runSync(Runnable run) {
	    BS.scheduleSyncDelayedTask(PLUGIN, run);
	}
}