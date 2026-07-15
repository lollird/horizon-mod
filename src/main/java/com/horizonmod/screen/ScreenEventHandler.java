package com.horizonmod.screen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Handle key press events for opening config screen
 */
@Environment(EnvType.CLIENT)
public class ScreenEventHandler implements ClientModInitializer {
	public static void registerKeyInputs() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (KeyBindings.OPEN_CONFIG.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new HorizonConfigScreen(null));
				}
			}
		});
	}

	@Override
	public void onInitializeClient() {
		// This will be called from HorizonModClient
	}
}
