package com.horizonmod.screen;

import com.horizonmod.HorizonModClient;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Keyboard binding for opening Horizon Mod configuration screen
 */
@Environment(EnvType.CLIENT)
public class KeyBindings {
	public static final KeyBinding OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"key.horizonmod.open_config",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			"category.horizonmod.config"
		)
	);
}
