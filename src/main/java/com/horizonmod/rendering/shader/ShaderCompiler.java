package com.horizonmod.rendering.shader;

import org.lwjgl.opengl.GL45;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compiles GLSL shader source code
 */
public class ShaderCompiler {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	/**
	 * Compile vertex shader
	 */
	public static int compileVertexShader(String source) {
		return compileShader(source, GL45.GL_VERTEX_SHADER, "vertex");
	}

	/**
	 * Compile fragment shader
	 */
	public static int compileFragmentShader(String source) {
		return compileShader(source, GL45.GL_FRAGMENT_SHADER, "fragment");
	}

	/**
	 * Compile geometry shader
	 */
	public static int compileGeometryShader(String source) {
		return compileShader(source, GL45.GL_GEOMETRY_SHADER, "geometry");
	}

	/**
	 * Internal shader compilation
	 */
	private static int compileShader(String source, int shaderType, String typeName) {
		try {
			int shaderId = GL45.glCreateShader(shaderType);
			GL45.glShaderSource(shaderId, source);
			GL45.glCompileShader(shaderId);

			int compileStatus = GL45.glGetShaderi(shaderId, GL45.GL_COMPILE_STATUS);
			if (compileStatus == 0) {
				String log = GL45.glGetShaderInfoLog(shaderId);
				LOGGER.error("Failed to compile {} shader:\n{}", typeName, log);
				GL45.glDeleteShader(shaderId);
				return -1;
			}

			LOGGER.debug("{} shader compiled successfully", typeName);
			return shaderId;
		} catch (Exception e) {
			LOGGER.error("Error compiling {} shader", typeName, e);
			return -1;
		}
	}
}
