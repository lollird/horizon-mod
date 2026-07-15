package com.horizonmod.rendering.shader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for shader assets
 */
public class ShaderRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final ShaderCache cache;
	private final Map<String, ShaderAsset> assets = new HashMap<>();

	public ShaderRegistry() {
		this.cache = new ShaderCache();
	}

	/**
	 * Register a shader asset
	 */
	public void register(String name, ShaderAsset asset) {
		assets.put(name, asset);
		LOGGER.debug("Registered shader asset: {}", name);
	}

	/**
	 * Get or load a shader program
	 */
	public ShaderProgram getProgram(String name) {
		ShaderAsset asset = assets.get(name);
		if (asset == null) {
			LOGGER.warn("Shader asset not found: {}", name);
			return null;
		}

		return cache.getOrCompile(name, asset.vertexSource, asset.fragmentSource);
	}

	/**
	 * Load default shaders
	 */
	public void loadDefaults() {
		// LOD shader for rendering distant chunks
		register("lod_basic", new ShaderAsset(
			getDefaultVertexShader(),
			getDefaultFragmentShader()
		));

		LOGGER.info("Loaded {} default shaders", 1);
	}

	/**
	 * Get shader cache
	 */
	public ShaderCache getCache() {
		return cache;
	}

	/**
	 * Cleanup all shaders
	 */
	public void cleanup() {
		cache.clear();
		assets.clear();
		LOGGER.info("Shader registry cleaned up");
	}

	/**
	 * Get default vertex shader source
	 */
	private static String getDefaultVertexShader() {
		return "#version 450 core\n" +
			"layout (location = 0) in vec3 position;\n" +
			"layout (location = 1) in vec3 normal;\n" +
			"\n" +
			"uniform mat4 projection;\n" +
			"uniform mat4 view;\n" +
			"uniform mat4 model;\n" +
			"\n" +
			"out VS_OUT {\n" +
			"    vec3 normal;\n" +
			"    vec3 fragPos;\n" +
			"} vs_out;\n" +
			"\n" +
			"void main() {\n" +
			"    gl_Position = projection * view * model * vec4(position, 1.0);\n" +
			"    vs_out.fragPos = vec3(model * vec4(position, 1.0));\n" +
			"    vs_out.normal = normalize(vec3(model * vec4(normal, 0.0)));\n" +
			"}";
	}

	/**
	 * Get default fragment shader source
	 */
	private static String getDefaultFragmentShader() {
		return "#version 450 core\n" +
			"in VS_OUT {\n" +
			"    vec3 normal;\n" +
			"    vec3 fragPos;\n" +
			"} fs_in;\n" +
			"\n" +
			"uniform vec3 lightDir;\n" +
			"uniform vec3 color;\n" +
			"\n" +
			"out vec4 FragColor;\n" +
			"\n" +
			"void main() {\n" +
			"    float diff = max(dot(fs_in.normal, lightDir), 0.0);\n" +
			"    vec3 result = color * diff;\n" +
			"    FragColor = vec4(result, 1.0);\n" +
			"}";
	}

	/**
	 * Shader asset data
	 */
	public static class ShaderAsset {
		public final String vertexSource;
		public final String fragmentSource;

		public ShaderAsset(String vertex, String fragment) {
			this.vertexSource = vertex;
			this.fragmentSource = fragment;
		}
	}
}
