package com.horizonmod.util;

/**
 * Utility class for timing code sections
 */
public class ProfiledTimer {
	private long startTimeNs;
	private final String name;
	private final PerformanceMonitor performanceMonitor;

	public ProfiledTimer(String name, PerformanceMonitor performanceMonitor) {
		this.name = name;
		this.performanceMonitor = performanceMonitor;
		this.startTimeNs = System.nanoTime();
	}

	/**
	 * Stop timing and record in performance monitor
	 */
	public long stop() {
		long elapsedNs = System.nanoTime() - startTimeNs;
		if (performanceMonitor != null) {
			performanceMonitor.endTimer(name);
		}
		return elapsedNs;
	}

	/**
	 * Get elapsed time without stopping
	 */
	public long getElapsedNs() {
		return System.nanoTime() - startTimeNs;
	}

	/**
	 * Get elapsed time in milliseconds
	 */
	public long getElapsedMs() {
		return getElapsedNs() / 1_000_000;
	}

	/**
	 * Reset the timer
	 */
	public void reset() {
		this.startTimeNs = System.nanoTime();
	}
}
