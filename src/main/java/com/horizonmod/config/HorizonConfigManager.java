package com.horizonmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.horizonmod.HorizonMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages loading and saving configuration files
 */
public class HorizonConfigManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(HorizonMod.MOD_ID);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String CONFIG_DIR = "config/horizonmod";
	private static final String CONFIG_FILE = "horizonmod.json";

	/**
	 * Load configuration from file, or create default if doesn't exist
	 */
	public static void loadConfig(HorizonConfig config) {
		try {
			Path configDir = Paths.get(CONFIG_DIR);
			Path configFile = configDir.resolve(CONFIG_FILE);

			// Create config directory if it doesn't exist
			if (!Files.exists(configDir)) {
				Files.createDirectories(configDir);
				LOGGER.info("Created config directory: {}", configDir);
			}

			// Load existing config or create new one
			if (Files.exists(configFile)) {
				try (FileReader reader = new FileReader(configFile.toFile())) {
					HorizonConfigData data = GSON.fromJson(reader, HorizonConfigData.class);
					if (data != null) {
						data.applyToConfig(config);
						LOGGER.info("Loaded configuration from {}", configFile);
					}
				}
			} else {
				// Save default config
				saveConfig(config);
				LOGGER.info("Created default configuration at {}", configFile);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load configuration", e);
		}
	}

	/**
	 * Save configuration to file
	 */
	public static void saveConfig(HorizonConfig config) {
		try {
			Path configDir = Paths.get(CONFIG_DIR);
			Path configFile = configDir.resolve(CONFIG_FILE);

			// Ensure directory exists
			if (!Files.exists(configDir)) {
				Files.createDirectories(configDir);
			}

			HorizonConfigData data = HorizonConfigData.fromConfig(config);
			try (FileWriter writer = new FileWriter(configFile.toFile())) {
				GSON.toJson(data, writer);
				LOGGER.info("Saved configuration to {}", configFile);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to save configuration", e);
		}
	}

	/**
	 * Internal DTO for JSON serialization
	 */
	public static class HorizonConfigData {
		public int renderDistance = 1000;
		public int lodLevels = 8;
		public boolean enableOpenGLOptimizations = true;
		public int targetFPS = 60;
		public boolean enableFrustumCulling = true;
		public boolean enableOcclusionCulling = false;
		public float lodDetailThreshold = 0.5f;
		public boolean adaptiveQuality = true;
		public int maxDrawDistance = 1500;
		public boolean debugMode = false;
		public boolean showLODBoundaries = false;

		public void applyToConfig(HorizonConfig config) {
			config.setRenderDistance(renderDistance);
			config.setLodLevels(lodLevels);
			config.setTargetFPS(targetFPS);
			config.setAdaptiveQuality(adaptiveQuality);
		}

		public static HorizonConfigData fromConfig(HorizonConfig config) {
			HorizonConfigData data = new HorizonConfigData();
			data.renderDistance = config.getRenderDistance();
			data.lodLevels = config.getLodLevels();
			data.enableOpenGLOptimizations = config.isOpenGLOptimizationsEnabled();
			data.targetFPS = config.getTargetFPS();
			data.enableFrustumCulling = config.isFrustumCullingEnabled();
			data.enableOcclusionCulling = config.isOcclusionCullingEnabled();
			data.lodDetailThreshold = config.getLodDetailThreshold();
			data.adaptiveQuality = config.isAdaptiveQualityEnabled();
			data.maxDrawDistance = config.getMaxDrawDistance();
			data.debugMode = config.isDebugMode();
			data.showLODBoundaries = config.shouldShowLODBoundaries();
			return data;
		}
	}
}
