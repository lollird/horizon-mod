package com.horizonmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HorizonMod implements ModInitializer {
	public static final String MOD_ID = "horizonmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Horizon Mod initialized!");
	}
}
