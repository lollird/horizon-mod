package com.horizonmod.mixin;

import com.horizonmod.HorizonModClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for injecting LOD rendering into the world renderer
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

	/**
	 * Hook into the chunk rendering setup to apply LOD levels
	 */
	@Inject(
		method = "render",
		at = @At("HEAD")
	)
	private void onRenderStart(CallbackInfo ci) {
		// Called at the start of each render frame
		if (HorizonModClient.getLODRenderSystem() != null) {
			HorizonModClient.getLODRenderSystem().onRenderTick();
		}
	}
}
