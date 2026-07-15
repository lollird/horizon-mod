package com.horizonmod.rendering.shader;

import org.lwjgl.opengl.GL45;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Compiled shader program with uniform management
 */
public class ShaderProgram {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final String name;
	private final int programId;
	private final Map<String, Integer> uniformLocations = new HashMap<>();
	private boolean linked = false;

	public ShaderProgram(String name) {
		this.name = name;
		this.programId = GL45.glCreateProgram();
	}

	/**
	 * Attach a compiled shader to this program
	 */
	public void attachShader(int shaderId) {
		try {
			GL45.glAttachShader(programId, shaderId);
		} catch (Exception e) {
			LOGGER.error("Failed to attach shader to program {}", name, e);
		}
	}

	/**
	 * Link the shader program
	 */
	public boolean link() {
		try {
			GL45.glLinkProgram(programId);

			int linkStatus = GL45.glGetProgrami(programId, GL45.GL_LINK_STATUS);
			if (linkStatus == 0) {
				String log = GL45.glGetProgramInfoLog(programId);
				LOGGER.error("Failed to link shader program {}: {}", name, log);
				return false;
			}

			linked = true;
			LOGGER.info("Shader program {} linked successfully", name);
			return true;
		} catch (Exception e) {
			LOGGER.error("Error linking shader program {}", name, e);
			return false;
		}
	}

	/**
	 * Use this shader program
	 */
	public void use() {
		if (linked) {
			GL45.glUseProgram(programId);
		}
	}

	/**
	 * Stop using any shader program
	 */
	public static void useNone() {
		GL45.glUseProgram(0);
	}

	/**
	 * Get or cache uniform location
	 */
	private int getUniformLocation(String name) {
		if (uniformLocations.containsKey(name)) {
			return uniformLocations.get(name);
		}

		int location = GL45.glGetUniformLocation(programId, name);
		if (location == -1) {
			LOGGER.warn("Uniform '{}' not found in program {}", name, this.name);
		}

		uniformLocations.put(name, location);
		return location;
	}

	/**
	 * Set uniform float
	 */
	public void setUniform1f(String name, float value) {
		int location = getUniformLocation(name);
		if (location >= 0) {
			GL45.glUniform1f(location, value);
		}
	}

	/**
	 * Set uniform vec2
	 */
	public void setUniform2f(String name, float x, float y) {
		int location = getUniformLocation(name);
		if (location >= 0) {
			GL45.glUniform2f(location, x, y);
		}
	}

	/**
	 * Set uniform vec3
	 */
	public void setUniform3f(String name, float x, float y, float z) {
		int location = getUniformLocation(name);
		if (location >= 0) {
			GL45.glUniform3f(location, x, y, z);
		}
	}

	/**
	 * Set uniform vec4
	 */
	public void setUniform4f(String name, float x, float y, float z, float w) {
		int location = getUniformLocation(name);
		if (location >= 0) {
			GL45.glUniform4f(location, x, y, z, w);
		}
	}

	/**
	 * Set uniform integer
	 */
	public void setUniform1i(String name, int value) {
		int location = getUniformLocation(name);
		if (location >= 0) {
			GL45.glUniform1i(location, value);
		}
	}

	/**
	 * Set uniform matrix
	 */
	public void setUniformMatrix4f(String name, FloatBuffer matrix) {
		int location = getUniformLocation(name);
		if (location >= 0) {
			GL45.glUniformMatrix4fv(location, false, matrix);
		}
	}

	/**
	 * Cleanup shader program
	 */
	public void cleanup() {
		try {
			GL45.glDeleteProgram(programId);
			LOGGER.debug("Shader program {} cleaned up", name);
		} catch (Exception e) {
			LOGGER.error("Error cleaning up shader program {}", name, e);
		}
	}

	public String getName() {
		return name;
	}

	public int getProgramId() {
		return programId;
	}

	public boolean isLinked() {
		return linked;
	}
}
