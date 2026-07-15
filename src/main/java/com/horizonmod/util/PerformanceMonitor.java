package com.horizonmod.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Real-time performance monitoring
 */
public class PerformanceMonitor {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");
	private static final int FRAME_HISTORY_SIZE = 120; // Track last 120 frames (2 seconds at 60 FPS)

	private final long[] frameTimesMs = new long[FRAME_HISTORY_SIZE];
	private int frameIndex = 0;
	private long lastFrameStartTime = System.nanoTime();
	private double currentFPS = 60.0;
	private double averageFPS = 60.0;
	private long minFrameTimeNs = Long.MAX_VALUE;
	private long maxFrameTimeNs = 0;
	private long totalFramesProcessed = 0;

	private final Map<String, TimerData> timerData = new HashMap<>();

	/**
	 * Call this at the start of each frame
	 */
	public void startFrame() {
		lastFrameStartTime = System.nanoTime();
	}

	/**
	 * Call this at the end of each frame
	 */
	public void endFrame() {
		long frameTimeNs = System.nanoTime() - lastFrameStartTime;
		long frameTimeMs = frameTimeNs / 1_000_000;

		// Store frame time
		frameTimesMs[frameIndex] = frameTimeMs;
		frameIndex = (frameIndex + 1) % FRAME_HISTORY_SIZE;

		// Update metrics
		if (frameTimeNs > 0) {
			currentFPS = 1_000_000_000.0 / frameTimeNs;
		}

		minFrameTimeNs = Math.min(minFrameTimeNs, frameTimeNs);
		maxFrameTimeNs = Math.max(maxFrameTimeNs, frameTimeNs);

		averageFrameTime = (averageFrameTime * 0.95) + (frameTimeMs * 0.05); // Exponential moving average
		averageFPS = 1000.0 / averageFrameTime;

		totalFramesProcessed++;
	}

	/**
	 * Start timing a specific operation
	 */
	public void startTimer(String name) {
		timerData.computeIfAbsent(name, k -> new TimerData())
			.startTime = System.nanoTime();
	}

	/**
	 * End timing a specific operation
	 */
	public void endTimer(String name) {
		TimerData data = timerData.get(name);
		if (data != null && data.startTime != 0) {
			long elapsedNs = System.nanoTime() - data.startTime;
			data.recordTime(elapsedNs);
		}
	}

	// Getters
	public double getCurrentFPS() {
		return currentFPS;
	}

	public double getAverageFPS() {
		return averageFPS;
	}

	public long getCurrentFrameTimeMs() {
		return frameTimesMs[(frameIndex - 1 + FRAME_HISTORY_SIZE) % FRAME_HISTORY_SIZE];
	}

	public double getAverageFrameTimeMs() {
		return averageFrameTime;
	}

	public long getMinFrameTimeMs() {
		return minFrameTimeNs / 1_000_000;
	}

	public long getMaxFrameTimeMs() {
		return maxFrameTimeNs / 1_000_000;
	}

	public long getTotalFramesProcessed() {
		return totalFramesProcessed;
	}

	public Map<String, TimerData> getTimerData() {
		return timerData;
	}

	private double averageFrameTime = 16.67; // Start at 60 FPS equivalent

	/**
	 * Timer data for a specific operation
	 */
	public static class TimerData {
		private long startTime = 0;
		private long totalTimeNs = 0;
		private long callCount = 0;
		private long minTimeNs = Long.MAX_VALUE;
		private long maxTimeNs = 0;

		private void recordTime(long elapsedNs) {
			totalTimeNs += elapsedNs;
			callCount++;
			minTimeNs = Math.min(minTimeNs, elapsedNs);
			maxTimeNs = Math.max(maxTimeNs, elapsedNs);
			startTime = 0;
		}

		public long getAverageTimeMs() {
			return callCount > 0 ? (totalTimeNs / callCount) / 1_000_000 : 0;
		}

		public long getTotalTimeMs() {
			return totalTimeNs / 1_000_000;
		}

		public long getCallCount() {
			return callCount;
		}

		public long getMinTimeMs() {
			return minTimeNs == Long.MAX_VALUE ? 0 : minTimeNs / 1_000_000;
		}

		public long getMaxTimeMs() {
			return maxTimeNs / 1_000_000;
		}

		public void reset() {
			totalTimeNs = 0;
			callCount = 0;
			minTimeNs = Long.MAX_VALUE;
			maxTimeNs = 0;
		}
	}
}
