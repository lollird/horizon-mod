package com.horizonmod.util;

import com.horizonmod.HorizonModClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * On-screen debug overlay displaying performance metrics
 */
public class DebugOverlay {
	private static final int TEXT_COLOR = 0x00FF00; // Green
	private static final int BACKGROUND_COLOR = 0x2F000000; // Semi-transparent black
	private static final int PADDING = 5;
	private static final int LINE_HEIGHT = 10;

	private final PerformanceMonitor performanceMonitor;
	private boolean enabled = false;

	public DebugOverlay(PerformanceMonitor performanceMonitor) {
		this.performanceMonitor = performanceMonitor;
	}

	/**
	 * Toggle debug overlay visibility
	 */
	public void toggle() {
		enabled = !enabled;
	}

	/**
	 * Render the debug overlay
	 */
	public void render(DrawContext context, int screenWidth, int screenHeight, net.minecraft.client.font.TextRenderer textRenderer) {
		if (!enabled || HorizonModClient.getConfig() == null) {
			return;
		}

		List<String> lines = buildDebugText();

		// Calculate overlay dimensions
		int overlayWidth = 250;
		int overlayHeight = PADDING * 2 + (lines.size() * LINE_HEIGHT);
		int x = 10;
		int y = 10;

		// Draw background
		context.fill(x, y, x + overlayWidth, y + overlayHeight, BACKGROUND_COLOR);

		// Draw text
		int textY = y + PADDING;
		for (String line : lines) {
			context.drawText(textRenderer, line, x + PADDING, textY, TEXT_COLOR, false);
			textY += LINE_HEIGHT;
		}
	}

	/**
	 * Build debug text lines
	 */
	private List<String> buildDebugText() {
		List<String> lines = new ArrayList<>();
		var config = HorizonModClient.getConfig();
		var lodSystem = HorizonModClient.getLODRenderSystem();

		// FPS and frame time
		lines.add(String.format("FPS: %.1f (avg: %.1f)",
			performanceMonitor.getCurrentFPS(),
			performanceMonitor.getAverageFPS()));
		lines.add(String.format("Frame: %.2f ms (avg: %.2f ms)",
			performanceMonitor.getCurrentFrameTimeMs(),
			performanceMonitor.getAverageFrameTimeMs()));
		lines.add(String.format("Min/Max: %.2f/%.2f ms",
			performanceMonitor.getMinFrameTimeMs(),
			performanceMonitor.getMaxFrameTimeMs()));

		lines.add(""); // Blank line

		// Configuration
		lines.add("Configuration:");
		lines.add(String.format("  Render Distance: %d chunks",
			config.getRenderDistance()));
		lines.add(String.format("  LOD Levels: %d",
			config.getLodLevels()));
		lines.add(String.format("  Target FPS: %d",
			config.getTargetFPS()));
		lines.add(String.format("  LOD Quality: %d%%",
			lodSystem.getLODManager().getCurrentLODQuality()));

		lines.add(""); // Blank line

		// Chunk cache statistics
		var cacheStats = lodSystem.getChunkSimplificationManager().getCacheStats();
		lines.add("Chunk Cache:");
		lines.add(String.format("  Cached Chunks: %d",
			cacheStats.chunksInCache));
		lines.add(String.format("  Total Vertices: %d",
			cacheStats.totalVertices));

		lines.add(""); // Blank line
		lines.add("Press 'G' to toggle this overlay");

		return lines;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
