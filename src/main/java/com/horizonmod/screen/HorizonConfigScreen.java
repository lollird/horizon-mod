package com.horizonmod.screen;

import com.horizonmod.HorizonModClient;
import com.horizonmod.config.HorizonConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/**
 * Main configuration screen for Horizon Mod
 */
public class HorizonConfigScreen extends Screen {
	private final Screen parent;
	private final HorizonConfig config;

	// UI Elements
	private SliderWidget renderDistanceSlider;
	private SliderWidget lodLevelsSlider;
	private SliderWidget targetFpsSlider;
	private ButtonWidget frustumCullingToggle;
	private ButtonWidget adaptiveQualityToggle;
	private ButtonWidget debugModeToggle;
	private ButtonWidget doneButton;

	private static final int SLIDER_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SPACING = 25;

	public HorizonConfigScreen(Screen parent) {
		super(Text.literal("Horizon Mod Settings"));
		this.parent = parent;
		this.config = HorizonModClient.getConfig();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int startY = 40;
		int currentY = startY;

		// Render Distance Slider
		this.renderDistanceSlider = new RenderDistanceSlider(centerX - SLIDER_WIDTH / 2, currentY,
			SLIDER_WIDTH, BUTTON_HEIGHT, config.getRenderDistance());
		this.addDrawableChild(this.renderDistanceSlider);
		currentY += SPACING;

		// LOD Levels Slider
		this.lodLevelsSlider = new LODLevelsSlider(centerX - SLIDER_WIDTH / 2, currentY,
			SLIDER_WIDTH, BUTTON_HEIGHT, config.getLodLevels());
		this.addDrawableChild(this.lodLevelsSlider);
		currentY += SPACING;

		// Target FPS Slider
		this.targetFpsSlider = new TargetFpsSlider(centerX - SLIDER_WIDTH / 2, currentY,
			SLIDER_WIDTH, BUTTON_HEIGHT, config.getTargetFPS());
		this.addDrawableChild(this.targetFpsSlider);
		currentY += SPACING + 10;

		// Frustum Culling Toggle
		this.frustumCullingToggle = ButtonWidget.builder(
			Text.literal("Frustum Culling: " + (config.isFrustumCullingEnabled() ? "ON" : "OFF")),
			button -> {
				// Toggle will be handled in onClose
			})
			.dimensions(centerX - SLIDER_WIDTH / 2, currentY, SLIDER_WIDTH, BUTTON_HEIGHT)
			.build();
		this.addDrawableChild(this.frustumCullingToggle);
		currentY += SPACING;

		// Adaptive Quality Toggle
		this.adaptiveQualityToggle = ButtonWidget.builder(
			Text.literal("Adaptive Quality: " + (config.isAdaptiveQualityEnabled() ? "ON" : "OFF")),
			button -> {
				// Toggle will be handled in onClose
			})
			.dimensions(centerX - SLIDER_WIDTH / 2, currentY, SLIDER_WIDTH, BUTTON_HEIGHT)
			.build();
		this.addDrawableChild(this.adaptiveQualityToggle);
		currentY += SPACING;

		// Debug Mode Toggle
		this.debugModeToggle = ButtonWidget.builder(
			Text.literal("Debug Mode: " + (config.isDebugMode() ? "ON" : "OFF")),
			button -> {
				// Toggle will be handled in onClose
			})
			.dimensions(centerX - SLIDER_WIDTH / 2, currentY, SLIDER_WIDTH, BUTTON_HEIGHT)
			.build();
		this.addDrawableChild(this.debugModeToggle);
		currentY += SPACING + 15;

		// Done Button
		this.doneButton = ButtonWidget.builder(Text.literal("Done"),
			button -> this.close())
			.dimensions(centerX - SLIDER_WIDTH / 2, currentY, SLIDER_WIDTH, BUTTON_HEIGHT)
			.build();
		this.addDrawableChild(this.doneButton);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

		// Draw labels
		int centerX = this.width / 2;
		int labelX = centerX - SLIDER_WIDTH / 2 - 10;

		context.drawText(this.textRenderer,
			Text.literal("Render Distance: " + this.renderDistanceSlider.getIntValue() + " chunks"),
			labelX, this.renderDistanceSlider.getY() + 3, 0xAAAAAA, false);

		context.drawText(this.textRenderer,
			Text.literal("LOD Levels: " + this.lodLevelsSlider.getIntValue()),
			labelX, this.lodLevelsSlider.getY() + 3, 0xAAAAAA, false);

		context.drawText(this.textRenderer,
			Text.literal("Target FPS: " + this.targetFpsSlider.getIntValue()),
			labelX, this.targetFpsSlider.getY() + 3, 0xAAAAAA, false);

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		// Apply slider values
		config.setRenderDistance(this.renderDistanceSlider.getIntValue());
		config.setLodLevels(this.lodLevelsSlider.getIntValue());
		config.setTargetFPS(this.targetFpsSlider.getIntValue());

		this.client.setScreen(this.parent);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	/**
	 * Custom slider for render distance (32-2048 chunks)
	 */
	private static class RenderDistanceSlider extends SliderWidget {
		public RenderDistanceSlider(int x, int y, int width, int height, int initialValue) {
			super(x, y, width, height, Text.literal(""), (initialValue - 32.0) / (2048.0 - 32.0));
		}

		public int getIntValue() {
			return (int) (32 + this.value * (2048 - 32));
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.literal("Render Distance: " + getIntValue()));
		}
	}

	/**
	 * Custom slider for LOD levels (1-16)
	 */
	private static class LODLevelsSlider extends SliderWidget {
		public LODLevelsSlider(int x, int y, int width, int height, int initialValue) {
			super(x, y, width, height, Text.literal(""), (initialValue - 1.0) / (16.0 - 1.0));
		}

		public int getIntValue() {
			return (int) (1 + this.value * (16 - 1));
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.literal("LOD Levels: " + getIntValue()));
		}
	}

	/**
	 * Custom slider for target FPS (30-240)
	 */
	private static class TargetFpsSlider extends SliderWidget {
		public TargetFpsSlider(int x, int y, int width, int height, int initialValue) {
			super(x, y, width, height, Text.literal(""), (initialValue - 30.0) / (240.0 - 30.0));
		}

		public int getIntValue() {
			return (int) (30 + this.value * (240 - 30));
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.literal("Target FPS: " + getIntValue()));
		}
	}
}
