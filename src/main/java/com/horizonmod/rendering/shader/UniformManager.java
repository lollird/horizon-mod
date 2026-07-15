package com.horizonmod.rendering.shader;

import org.lwjgl.opengl.GL45;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages GPU uniform variables for shaders
 */
public class UniformManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final ShaderProgram program;
	private final Map<String, UniformValue> uniforms = new HashMap<>();

	public UniformManager(ShaderProgram program) {
		this.program = program;
	}

	/**
	 * Set float uniform
	 */
	public void setFloat(String name, float value) {
		uniforms.put(name, new FloatValue(value));
		program.setUniform1f(name, value);
	}

	/**
	 * Set int uniform
	 */
	public void setInt(String name, int value) {
		uniforms.put(name, new IntValue(value));
		program.setUniform1i(name, value);
	}

	/**
	 * Set vec2 uniform
	 */
	public void setVec2(String name, float x, float y) {
		uniforms.put(name, new Vec2Value(x, y));
		program.setUniform2f(name, x, y);
	}

	/**
	 * Set vec3 uniform
	 */
	public void setVec3(String name, float x, float y, float z) {
		uniforms.put(name, new Vec3Value(x, y, z));
		program.setUniform3f(name, x, y, z);
	}

	/**
	 * Set vec4 uniform
	 */
	public void setVec4(String name, float x, float y, float z, float w) {
		uniforms.put(name, new Vec4Value(x, y, z, w));
		program.setUniform4f(name, x, y, z, w);
	}

	/**
	 * Set matrix uniform
	 */
	public void setMatrix4f(String name, FloatBuffer matrix) {
		uniforms.put(name, new MatrixValue(matrix));
		program.setUniformMatrix4f(name, matrix);
	}

	/**
	 * Get a cached uniform value
	 */
	public UniformValue getUniform(String name) {
		return uniforms.get(name);
	}

	/**
	 * Apply all cached uniforms to shader
	 */
	public void applyAll() {
		program.use();
		for (Map.Entry<String, UniformValue> entry : uniforms.entrySet()) {
			entry.getValue().apply(program, entry.getKey());
		}
	}

	/**
	 * Base class for uniform values
	 */
	public static abstract class UniformValue {
		abstract void apply(ShaderProgram program, String name);
	}

	private static class FloatValue extends UniformValue {
		final float value;

		FloatValue(float value) {
			this.value = value;
		}

		@Override
		void apply(ShaderProgram program, String name) {
			program.setUniform1f(name, value);
		}
	}

	private static class IntValue extends UniformValue {
		final int value;

		IntValue(int value) {
			this.value = value;
		}

		@Override
		void apply(ShaderProgram program, String name) {
			program.setUniform1i(name, value);
		}
	}

	private static class Vec2Value extends UniformValue {
		final float x, y;

		Vec2Value(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		void apply(ShaderProgram program, String name) {
			program.setUniform2f(name, x, y);
		}
	}

	private static class Vec3Value extends UniformValue {
		final float x, y, z;

		Vec3Value(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		void apply(ShaderProgram program, String name) {
			program.setUniform3f(name, x, y, z);
		}
	}

	private static class Vec4Value extends UniformValue {
		final float x, y, z, w;

		Vec4Value(float x, float y, float z, float w) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}

		@Override
		void apply(ShaderProgram program, String name) {
			program.setUniform4f(name, x, y, z, w);
		}
	}

	private static class MatrixValue extends UniformValue {
		final FloatBuffer matrix;

		MatrixValue(FloatBuffer matrix) {
			this.matrix = matrix;
		}

		@Override
		void apply(ShaderProgram program, String name) {
			program.setUniformMatrix4f(name, matrix);
		}
	}
}
