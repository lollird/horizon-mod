package com.horizonmod.rendering.culling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrustumCuller {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");
	
	private final Plane[] frustumPlanes = new Plane[6]; // Left, Right, Top, Bottom, Near, Far

	public FrustumCuller() {
		for (int i = 0; i < 6; i++) {
			frustumPlanes[i] = new Plane();
		}
		LOGGER.info("Frustum culler initialized");
	}

	/**
	 * Update frustum planes based on current camera position and view matrix
	 */
	public void updateFrustum() {
		// TODO: Extract frustum planes from projection matrix
		// This requires access to camera matrices which will be injected via mixin
	}

	/**
	 * Check if a chunk is within the view frustum
	 */
	public boolean isChunkInFrustum(int chunkX, int chunkZ) {
		// Check if chunk AABB intersects with frustum
		// Chunk is 16x16 blocks
		float x1 = chunkX * 16;
		float z1 = chunkZ * 16;
		float x2 = x1 + 16;
		float z2 = z1 + 16;
		
		for (Plane plane : frustumPlanes) {
			if (!plane.intersectsAABB(x1, z1, x2, z2)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Check if a sphere is within the view frustum
	 */
	public boolean isSphereInFrustum(double x, double y, double z, double radius) {
		for (Plane plane : frustumPlanes) {
			if (plane.distanceToPoint(x, y, z) < -radius) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Internal plane class for frustum calculations
	 */
	private static class Plane {
		float a, b, c, d;

		Plane() {
			this.a = 0;
			this.b = 0;
			this.c = 1;
			this.d = 0;
		}

		double distanceToPoint(double x, double y, double z) {
			return a * x + b * y + c * z + d;
		}

		boolean intersectsAABB(float x1, float z1, float x2, float z2) {
			// Simple AABB-plane intersection test
			// Can be expanded for 3D bounding boxes
			float radius = (x2 - x1) / 2;
			double dist = distanceToPoint((x1 + x2) / 2, 0, (z1 + z2) / 2);
			return dist >= -radius;
		}
	}
}
