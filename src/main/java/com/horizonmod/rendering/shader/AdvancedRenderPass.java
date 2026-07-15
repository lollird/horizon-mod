package com.horizonmod.rendering.shader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages multi-pass rendering for advanced effects
 */
public class AdvancedRenderPass {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final String name;
	private final List<RenderPass> passes = new ArrayList<>();
	private int currentPass = 0;

	public AdvancedRenderPass(String name) {
		this.name = name;
	}

	/**
	 * Add a render pass
	 */
	public void addPass(String passName, ShaderProgram shader) {
		passes.add(new RenderPass(passName, shader));
		LOGGER.debug("Added render pass '{}' to pipeline '{}'", passName, name);
	}

	/**
	 * Begin rendering
	 */
	public void begin() {
		currentPass = 0;
		if (!passes.isEmpty()) {
			passes.get(0).shader.use();
		}
	}

	/**
	 * Move to next pass
	 */
	public boolean nextPass() {
		currentPass++;
		if (currentPass < passes.size()) {
			passes.get(currentPass).shader.use();
			return true;
		}
		return false;
	}

	/**
	 * End rendering
	 */
	public void end() {
		ShaderProgram.useNone();
	}

	/**
	 * Get current pass name
	 */
	public String getCurrentPassName() {
		if (currentPass < passes.size()) {
			return passes.get(currentPass).name;
		}
		return "none";
	}

	/**
	 * Get current shader program
	 */
	public ShaderProgram getCurrentShader() {
		if (currentPass < passes.size()) {
			return passes.get(currentPass).shader;
		}
		return null;
	}

	/**
	 * Get pass count
	 */
	public int getPassCount() {
		return passes.size();
	}

	/**
	 * Individual render pass
	 */
	private static class RenderPass {
		private final String name;
		private final ShaderProgram shader;

		RenderPass(String name, ShaderProgram shader) {
			this.name = name;
			this.shader = shader;
		}
	}

	public String getName() {
		return name;
	}
}
