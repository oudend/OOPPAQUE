package se.chalmers.group7.core.rendering;

import static com.raylib.Colors.BLANK;
import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.*;
import static se.chalmers.group7.core.canvas.utils.RaylibBlendModes.applyBlendMode;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerPreviewManager;
import se.chalmers.group7.core.layers.LayerSettings;

public class LayerRenderer {
  private final LayerManager layerManager;
  private final LayerPreviewManager layerPreviewManager;
  private final Raylib.RenderTexture renderTexture;

  private Raylib.RenderTexture intermediateTexture;

  public LayerRenderer(
      LayerManager layerManager, LayerPreviewManager layerPreviewManager, int width, int height) {
    this.layerManager = layerManager;
    this.layerPreviewManager = layerPreviewManager;
    renderTexture = LoadRenderTexture(width, height);
    intermediateTexture = LoadRenderTexture(width, height);
  }

  public Texture getTexture() {
    return renderTexture.texture();
  }

  private void ensureIntermediateTexture(int width, int height) {
    if (intermediateTexture.texture().width() == width
        && intermediateTexture.texture().height() == height) {
      return;
    }

    Raylib.UnloadRenderTexture(intermediateTexture);

    intermediateTexture = Raylib.LoadRenderTexture(width, height);
  }

  private Raylib.Texture createLayerComposite(
      Layer layer, Raylib.Texture previewTexture, BlendingMode previewBlendingMode) {
    Canvas layerCanvas = layer.getCanvas();

    int width = layerCanvas.getWidth();
    int height = layerCanvas.getHeight();

    ensureIntermediateTexture(width, height);

    Raylib.BeginTextureMode(intermediateTexture);

    Raylib.ClearBackground(BLANK);

    Raylib.Rectangle source = new Raylib.Rectangle().x(0).y(0).width(width).height(-height);

    Raylib.Rectangle destination = new Raylib.Rectangle().x(0).y(0).width(width).height(height);

    Raylib.Vector2 origin = new Raylib.Vector2().x(0).y(0);

    // Draw original layer.
    applyBlendMode(BlendingMode.REPLACE);

    Raylib.DrawTexturePro(layerCanvas.getTexture(), source, destination, origin, 0, WHITE);

    Raylib.EndBlendMode();

    // Draw preview on top of original layer.
    if (previewTexture != null) {
      source =
          new Raylib.Rectangle()
              .x(0)
              .y(0)
              .width(previewTexture.width())
              .height(-previewTexture.height());

      applyBlendMode(previewBlendingMode);

      Raylib.DrawTexturePro(previewTexture, source, destination, origin, 0, WHITE);

      Raylib.EndBlendMode();
    }

    Raylib.EndTextureMode();

    return intermediateTexture.texture();
  }

  private void renderLayerComposite(Layer layer, Raylib.Texture compositeTexture, Camera camera) {
    LayerSettings settings = layer.getLayerSettings();

    float zoom = camera.getZoom();

    Raylib.Vector2 screenPosition = camera.worldToScreen(layer.getX(), layer.getY());

    Raylib.Rectangle source =
        new Raylib.Rectangle()
            .x(0)
            .y(0)
            .width(compositeTexture.width())
            .height(-compositeTexture.height());

    // Layer's dimensions after zooming
    float width = layer.getWidth() * zoom;
    float height = layer.getHeight() * zoom;

    // Rotation pivot: center of the layer, in screen pixels
    Raylib.Vector2 origin = new Raylib.Vector2().x(width / 2.0f).y(height / 2.0f);

    // Destination position is the pivot's screen position
    Raylib.Rectangle destination =
        new Raylib.Rectangle()
            .x(screenPosition.x() + origin.x())
            .y(screenPosition.y() + origin.y())
            .width(width)
            .height(height);

    Raylib.Color tint = ColorAlpha(WHITE, settings.getOpacity());

    applyBlendMode(settings.getBlendingMode());

    Raylib.DrawTexturePro(compositeTexture, source, destination, origin, layer.getRotation(), tint);

    Raylib.EndBlendMode();
  }

  private void renderLayer(Layer layer, Camera camera) {
    Raylib.Texture compositeTexture = layer.getCanvas().getTexture();

    renderLayerComposite(layer, compositeTexture, camera);
  }

  private void renderLayer(
      Layer layer, Camera camera, Raylib.Texture previewTexture, BlendingMode previewBlendingMode) {
    Raylib.Texture compositeTexture =
        createLayerComposite(layer, previewTexture, previewBlendingMode);

    BeginTextureMode(renderTexture);
    renderLayerComposite(layer, compositeTexture, camera);
    EndTextureMode();
  }

  public void render(Camera camera) {
    Canvas previewCanvas = layerPreviewManager.getPreviewCanvas();
    BeginTextureMode(renderTexture);
    ClearBackground(BLANK);
    EndTextureMode();

    for (int i = 0; i < layerManager.getLayerCount(); i++) {
      Layer layer = layerManager.getLayer(i);

      if (i == layerManager.getActiveLayerIndex()) {
        if (previewCanvas != null) {
          Raylib.Texture previewTexture = previewCanvas.getTexture();
          BlendingMode previewBlending = layerPreviewManager.getPreviewBlending();

          renderLayer(layer, camera, previewTexture, previewBlending);
          continue;
        }
      }

      renderLayer(layer, camera);
    }
  }
}
