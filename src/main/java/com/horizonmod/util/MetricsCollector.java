package com.horizonmod.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects and aggregates performance metrics
 */
public class MetricsCollector {
	private final PerformanceMonitor performanceMonitor;
	private final Map<String, Long> counters = new HashMap<>();
	private final Map<String, Double> averages = new HashMap<>();
	private long sessionStartTime;

	public MetricsCollector(PerformanceMonitor performanceMonitor) {
		this.performanceMonitor = performanceMonitor;
		this.sessionStartTime = System.currentTimeMillis();
	}

	/**
	 * Increment a counter
	 */
	public void incrementCounter(String name) {
		counters.put(name, counters.getOrDefault(name, 0L) + 1);
	}

	/**
	 * Add to counter by value
	 */
	public void addToCounter(String name, long value) {
		counters.put(name, counters.getOrDefault(name, 0L) + value);
	}

	/**
	 * Record an average value
	 */
	public void recordAverage(String name, double value) {
		// Exponential moving average
		double current = averages.getOrDefault(name, value);
		averages.put(name, current * 0.9 + value * 0.1);
	}

	/**
	 * Get a counter value
	 */
	public long getCounter(String name) {
		return counters.getOrDefault(name, 0L);
	}

	/**
	 * Get an average value
	 */
	public double getAverage(String name) {
		return averages.getOrDefault(name, 0.0);
	}

	/**
	 * Get session uptime in milliseconds
	 */
	public long getSessionUptimeMs() {
		return System.currentTimeMillis() - sessionStartTime;
	}

	/**
	 * Generate a performance report
	 */
	public String generateReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n=== Horizon Mod Performance Report ===");
		sb.append(String.format("\nSession Uptime: %.1f seconds", getSessionUptimeMs() / 1000.0));
		sb.append(String.format("\nAverage FPS: %.1f", performanceMonitor.getAverageFPS()));
		sb.append(String.format("\nAverage Frame Time: %.2f ms", performanceMonitor.getAverageFrameTimeMs()));
		sb.append(String.format("\nTotal Frames: %d", performanceMonitor.getTotalFramesProcessed()));

		if (!counters.isEmpty()) {
			sb.append("\n\nCounters:");
			for (Map.Entry<String, Long> entry : counters.entrySet()) {
				sb.append(String.format("\n  %s: %d", entry.getKey(), entry.getValue()));
			}
		}

		if (!averages.isEmpty()) {
			sb.append("\n\nAverages:");
			for (Map.Entry<String, Double> entry : averages.entrySet()) {
				sb.append(String.format("\n  %s: %.2f", entry.getKey(), entry.getValue()));
			}
		}

		sb.append("\n=================================");
		return sb.toString();
	}

	/**
	 * Reset all metrics
	 */
	public void reset() {
		counters.clear();
		averages.clear();
		sessionStartTime = System.currentTimeMillis();
	}
}
