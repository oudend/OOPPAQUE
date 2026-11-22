package se.chalmers.group7.core.rendering;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import com.raylib.Raylib;

public class SquarePatternRenderer {
  RenderTexture patternRenderTexture;
  private final int squareSize;

  Color approximateColor;

  public SquarePatternRenderer(int squareSize, Color squareColor, Color backgroundColor) {
    patternRenderTexture = LoadRenderTexture(squareSize * 2, squareSize * 2);
    this.squareSize = squareSize;

    BeginTextureMode(patternRenderTexture);

    ClearBackground(backgroundColor);

    DrawRectangle(0, 0, squareSize, squareSize, squareColor);
    DrawRectangle(squareSize, squareSize, squareSize, squareSize, squareColor);

    EndTextureMode();

    SetTextureWrap(patternRenderTexture.texture(), TEXTURE_WRAP_REPEAT);

    approximateColor = ColorTint(squareColor, backgroundColor);
  }

  public SquarePatternRenderer() {
    this(10, LIGHTGRAY, WHITE);
  }

  public void render(Camera camera, int width, int height) {
    float zoom = camera.getZoom();

    Raylib.Vector2 screenPosition = camera.worldToScreen(0, 0);

    //    Raylib.Rectangle source = new Raylib.Rectangle().x(0).y(0).width(Math.min(width *
    // squareSize, width * zoom)).height(-Math.min(height * squareSize, height * zoom));

    float sourceWidth = Math.min(width * squareSize, width * zoom);
    float sourceHeight = Math.min(height * squareSize, height * zoom);

    // Layer's dimensions after zooming
    float finalWidth = width * zoom;
    float finalHeight = height * zoom;

    float screenCenterX = Raylib.GetScreenWidth() / 2.0f;
    float screenCenterY = Raylib.GetScreenHeight() / 2.0f;

    float sourceX =
        patternRenderTexture.texture().width() / 2.0f
            - (screenCenterX - screenPosition.x()) * (sourceWidth / finalWidth);
    float sourceY =
        patternRenderTexture.texture().height() / 2.0f
            - sourceHeight
            + (screenCenterY - screenPosition.y()) * (sourceHeight / finalHeight);

    if (zoom > squareSize) {
      sourceX = 0;
      sourceY = 0;
    }

    Raylib.Rectangle source =
        new Raylib.Rectangle().x(sourceX).y(sourceY).width(sourceWidth).height(-sourceHeight);

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

    //    DrawRectanglePro(
    //        destination,
    //        origin,
    //        0,
    //        ColorAlpha(approximateColor, Math.clamp((1.0f / (zoom * squareSize)) * 2.0f, 0.0f,
    // 1.0f)));
  }
}
