package com.horizonmod.screen;

import com.horizonmod.HorizonModClient;
import com.horizonmod.util.DebugOverlay;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Handle key press events for opening config screen and toggling debug overlay
 */
@Environment(EnvType.CLIENT)
public class ScreenEventHandler implements ScreenEventHandlerInterface {
	private static DebugOverlay debugOverlay;

	public static void registerKeyInputs() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Config screen
			if (KeyBindings.OPEN_CONFIG.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new HorizonConfigScreen(null));
				}
			}

			// Debug overlay toggle
			if (KeyBindings.TOGGLE_DEBUG_OVERLAY.wasPressed()) {
				if (debugOverlay != null) {
					debugOverlay.toggle();
				}
			}
		});
	}

	public static void setDebugOverlay(DebugOverlay overlay) {
		debugOverlay = overlay;
	}

	public static DebugOverlay getDebugOverlay() {
		return debugOverlay;
	}

	@Override
	public void onInitializeClient() {
		// This will be called from HorizonModClient
	}
}

interface ScreenEventHandlerInterface {
	void onInitializeClient();
}
