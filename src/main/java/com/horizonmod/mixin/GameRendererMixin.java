package com.horizonmod.mixin;

import com.horizonmod.HorizonModClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for injecting frame update hooks
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	/**
	 * Update LOD system at the beginning of each frame
	 */
	@Inject(
		method = "updateCameraAndRender",
		at = @At("HEAD")
	)
	private void onFrameStart(CallbackInfo ci) {
		// Called at the start of each frame update
		if (HorizonModClient.getLODRenderSystem() != null) {
			HorizonModClient.getLODRenderSystem().onRenderTick();
		}
	}
}
