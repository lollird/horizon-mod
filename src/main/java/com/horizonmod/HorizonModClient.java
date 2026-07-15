package com.horizonmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import com.horizonmod.rendering.LODRenderSystem;
import com.horizonmod.config.HorizonConfig;
import com.horizonmod.config.HorizonConfigManager;
import com.horizonmod.screen.ScreenEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class HorizonModClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(HorizonMod.MOD_ID);
	private static LODRenderSystem lodRenderSystem;
	private static HorizonConfig config;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Horizon Mod Client initialized!");
		
		// Initialize configuration
		config = new HorizonConfig();
		HorizonConfigManager.loadConfig(config);
		
		// Initialize LOD rendering system
		lodRenderSystem = new LODRenderSystem(config);
		LOGGER.info("LOD Render System initialized with render distance: {} chunks", config.getRenderDistance());
		
		// Register key bindings and screen events
		ScreenEventHandler.registerKeyInputs();
		LOGGER.info("Key bindings registered (Press 'H' to open config)");
	}

	public static LODRenderSystem getLODRenderSystem() {
		return lodRenderSystem;
	}

	public static HorizonConfig getConfig() {
		return config;
	}
}
