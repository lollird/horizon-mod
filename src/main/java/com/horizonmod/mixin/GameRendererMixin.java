package com.horizonmod.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin for injecting frame update hooks
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	// TODO: Add injection points for per-frame LOD updates
}
