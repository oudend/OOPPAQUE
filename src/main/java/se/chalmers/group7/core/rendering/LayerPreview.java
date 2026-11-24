package se.chalmers.group7.core.rendering;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;

public record LayerPreview(Raylib.Texture texture, BlendingMode blending) {}
