# Horizon Mod

A high-performance LOD (Level of Detail) rendering mod for Minecraft Fabric 26.3-snapshot-3, designed to enable 1000+ chunk render distances while maintaining 60 FPS.

## Features

- **Extreme Render Distance**: Supports 1000+ chunk render distances
- **LOD System**: 8 progressive detail levels for distant chunks
- **60 FPS Optimization**: Advanced performance tuning to maintain frame rate
- **OpenGL Acceleration**: Direct OpenGL 4.6+ optimizations for rendering
- **Frustum Culling**: Efficiently cull off-screen chunks
- **Adaptive Quality**: Automatically adjusts quality based on current FPS
- **Performance Monitoring**: Built-in FPS counter and debug info

## Installation

1. Install [Fabric Loader](https://fabricmc.net/)
2. Install [Fabric API](https://www.curseforge.com/minecraft/mods/fabric-api)
3. Download the latest Horizon Mod JAR
4. Place it in your `mods` folder
5. Launch Minecraft with Fabric profile

## Configuration

Configuration files will be stored in `config/horizonmod/`. Currently supports:
- Render distance (32-2048 chunks)
- LOD detail levels (1-16)
- Target FPS (30-240)
- Culling and optimization options

## Requirements

- Minecraft 1.21.3+
- Fabric Loader 0.16.3+
- OpenGL 4.6 capable GPU
- Java 21+

## Development

### Building from Source

```bash
git clone https://github.com/lollird/horizon-mod.git
cd horizon-mod
./gradlew build
```

The built JAR will be in `build/libs/`.

### Project Structure

```
src/main/java/com/horizonmod/
├── HorizonMod.java                 # Main mod entry
├── HorizonModClient.java           # Client initialization
├── config/
│   └── HorizonConfig.java          # Configuration system
├── rendering/
│   ├── LODRenderSystem.java        # Core rendering system
│   ├── lod/
│   │   └── LODManager.java         # LOD level management
│   ├── opengl/
│   │   └── OpenGLOptimizer.java    # OpenGL optimizations
│   └── culling/
│       └── FrustumCuller.java      # Visibility testing
└── mixin/
    ├── WorldRendererMixin.java     # Chunk rendering hooks
    └── GameRendererMixin.java      # Frame update hooks
```

## Performance Tips

1. Enable Frustum Culling for best performance
2. Adjust LOD levels based on your GPU capability
3. Use Adaptive Quality for automatic optimization
4. Monitor FPS with debug mode enabled

## Known Limitations

- Currently designed for single-player/LAN play
- Requires modern GPU with OpenGL 4.6 support
- Occlusion culling is not yet implemented

## Future Improvements

- [ ] Multiplayer server support
- [ ] Occlusion culling system
- [ ] Custom shader integration
- [ ] Advanced texture streaming
- [ ] Configuration GUI
- [ ] Performance profiling tools

## License

MIT License - See LICENSE file for details

## Support

For issues, feature requests, or contributions, please visit the [GitHub repository](https://github.com/lollird/horizon-mod).
