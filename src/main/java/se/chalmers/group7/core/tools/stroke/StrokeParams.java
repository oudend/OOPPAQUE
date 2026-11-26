package se.chalmers.group7.core.tools.stroke;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;

public record StrokeParams(
    float size,
    float opacity,
    Raylib.Color color,
    BlendingMode previewBlendMode,
    BlendingMode drawBlendMode) {}
