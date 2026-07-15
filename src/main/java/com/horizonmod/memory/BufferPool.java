package com.horizonmod.memory;

import org.lwjgl.opengl.GL45;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * GPU buffer pool for efficient buffer management
 */
public class BufferPool {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private static final int BUFFER_SIZE = 1024 * 1024; // 1MB per buffer
	private static final int MAX_BUFFERS = 32;

	private final Queue<Integer> availableBuffers = new LinkedList<>();
	private final Set<Integer> allocatedBuffers = new HashSet<>();
	private int buffersCreated = 0;

	/**
	 * Allocate a GPU buffer
	 */
	public synchronized int allocateBuffer() {
		Integer bufferId = availableBuffers.poll();

		if (bufferId == null) {
			if (buffersCreated >= MAX_BUFFERS) {
				LOGGER.warn("Buffer pool exhausted, max {} buffers", MAX_BUFFERS);
				return -1;
			}

			try {
				bufferId = GL45.glGenBuffers();
				buffersCreated++;
			} catch (Exception e) {
				LOGGER.error("Failed to allocate GPU buffer", e);
				return -1;
			}
		}

		allocatedBuffers.add(bufferId);
		return bufferId;
	}

	/**
	 * Release a GPU buffer back to pool
	 */
	public synchronized void releaseBuffer(int bufferId) {
		if (allocatedBuffers.remove(bufferId)) {
			availableBuffers.offer(bufferId);
		}
	}

	/**
	 * Get buffer statistics
	 */
	public synchronized BufferStats getStats() {
		return new BufferStats(buffersCreated, availableBuffers.size(), allocatedBuffers.size());
	}

	/**
	 * Cleanup all buffers
	 */
	public synchronized void cleanup() {
		try {
			for (Integer bufferId : allocatedBuffers) {
				GL45.glDeleteBuffers(bufferId);
			}
			for (Integer bufferId : availableBuffers) {
				GL45.glDeleteBuffers(bufferId);
			}
			allocatedBuffers.clear();
			availableBuffers.clear();
			buffersCreated = 0;
			LOGGER.info("Buffer pool cleaned up");
		} catch (Exception e) {
			LOGGER.error("Error cleaning up buffer pool", e);
		}
	}

	/**
	 * Buffer statistics
	 */
	public static class BufferStats {
		public final int totalBuffers;
		public final int availableBuffers;
		public final int allocatedBuffers;

		public BufferStats(int total, int available, int allocated) {
			this.totalBuffers = total;
			this.availableBuffers = available;
			this.allocatedBuffers = allocated;
		}

		public double getUtilization() {
			if (totalBuffers == 0) return 0;
			return (allocatedBuffers * 100.0) / totalBuffers;
		}

		@Override
		public String toString() {
			return String.format("Buffers: %d total, %d available, %d allocated (%.1f%% util)",
				totalBuffers, availableBuffers, allocatedBuffers, getUtilization());
		}
	}
}
