package se.chalmers.group7.core.rendering;

import static com.raylib.Colors.GRAY;
import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.*;

import com.raylib.Raylib;

public class TransparencyPatternRenderer {
  RenderTexture patternRenderTexture;

  public TransparencyPatternRenderer(int squareSize, Color squareColor, Color backgroundColor) {
    patternRenderTexture = LoadRenderTexture(squareSize * 2, squareSize * 2);

    BeginTextureMode(patternRenderTexture);

    ClearBackground(backgroundColor);

    DrawRectangle(0, 0, squareSize, squareSize, squareColor);
    DrawRectangle(squareSize, squareSize, squareSize, squareSize, squareColor);

    EndTextureMode();

    SetTextureWrap(patternRenderTexture.texture(), TEXTURE_WRAP_REPEAT);
  }

  public TransparencyPatternRenderer() {
    this(10, GRAY, WHITE);
  }

  public void render(Camera camera, int width, int height) {
    float zoom = camera.getZoom();

    Raylib.Vector2 screenPosition = camera.worldToScreen(0, 0);

    Raylib.Rectangle source =
        new Raylib.Rectangle()
            .x(screenPosition.x())
            .y(-screenPosition.y())
            .width(width * zoom)
            .height(-height * zoom);

    // Layer's dimensions after zooming
    float finalWidth = width * zoom;
    float finalHeight = height * zoom;

    // Rotation pivot: center of the layer, in screen pixels
    Raylib.Vector2 origin = new Raylib.Vector2().x(finalWidth / 2.0f).y(finalHeight / 2.0f);

    // Destination position is the pivot's screen position
    Raylib.Rectangle destination =
        new Raylib.Rectangle()
            .x(screenPosition.x() + origin.x())
            .y(screenPosition.y() + origin.y())
            .width(finalWidth)
            .height(finalHeight);

    Raylib.DrawTexturePro(patternRenderTexture.texture(), source, destination, origin, 0, WHITE);
  }
}
