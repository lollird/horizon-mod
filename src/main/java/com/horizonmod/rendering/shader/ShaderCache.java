package com.horizonmod.rendering.shader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Caches compiled shader programs to avoid recompilation
 */
public class ShaderCache {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final Map<String, ShaderProgram> cache = new HashMap<>();
	private int cacheHits = 0;
	private int cacheMisses = 0;

	/**
	 * Get or compile a shader program
	 */
	public ShaderProgram getOrCompile(String name, String vertexSource, String fragmentSource) {
		if (cache.containsKey(name)) {
			cacheHits++;
			LOGGER.debug("Shader cache hit: {}", name);
			return cache.get(name);
		}

		cacheMisses++;
		LOGGER.info("Compiling shader program: {}", name);

		// Compile shaders
		int vertexId = ShaderCompiler.compileVertexShader(vertexSource);
		int fragmentId = ShaderCompiler.compileFragmentShader(fragmentSource);

		if (vertexId == -1 || fragmentId == -1) {
			LOGGER.error("Failed to compile shaders for program {}", name);
			return null;
		}

		// Link program
		ShaderProgram program = new ShaderProgram(name);
		program.attachShader(vertexId);
		program.attachShader(fragmentId);

		if (!program.link()) {
			LOGGER.error("Failed to link shader program {}", name);
			program.cleanup();
			return null;
		}

		cache.put(name, program);
		return program;
	}

	/**
	 * Get cached shader program
	 */
	public ShaderProgram get(String name) {
		return cache.get(name);
	}

	/**
	 * Get cache statistics
	 */
	public CacheStats getStats() {
		int total = cacheHits + cacheMisses;
		double hitRate = total > 0 ? (cacheHits * 100.0) / total : 0;
		return new CacheStats(cache.size(), cacheHits, cacheMisses, hitRate);
	}

	/**
	 * Clear all cached shaders
	 */
	public void clear() {
		for (ShaderProgram program : cache.values()) {
			program.cleanup();
		}
		cache.clear();
		LOGGER.info("Shader cache cleared");
	}

	/**
	 * Cache statistics
	 */
	public static class CacheStats {
		public final int programsInCache;
		public final int cacheHits;
		public final int cacheMisses;
		public final double hitRate;

		public CacheStats(int programs, int hits, int misses, double hitRate) {
			this.programsInCache = programs;
			this.cacheHits = hits;
			this.cacheMisses = misses;
			this.hitRate = hitRate;
		}

		@Override
		public String toString() {
			return String.format("Shader Cache: %d programs, Hit Rate: %.1f%% (%d hits, %d misses)",
				programsInCache, hitRate, cacheHits, cacheMisses);
		}
	}
}
