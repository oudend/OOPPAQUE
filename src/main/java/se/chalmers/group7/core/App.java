package se.chalmers.group7.core;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerPreviewManager;
import se.chalmers.group7.core.layers.PixelLayer;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerRenderer;
import se.chalmers.group7.core.rendering.SquarePatternRenderer;
import se.chalmers.group7.core.tools.*;

public class App {

  private final int width;
  private final int height;

  private Tool selectedTool;

  private final LayerPreviewManager previewManager;
  private final LayerRenderer layerRenderer;
  private final LayerManager layerManager;

  private boolean mouseDown = false;

  private Vector2 previousMousePosition = Vector2Zero();

  private final OperationManager operationManager = new OperationManager();

  private final SquarePatternRenderer transparencyPatternRenderer;

  private final Tool[] tools;
  private Camera camera = new Camera();

  public App(int width, int height) {
    this.width = width;
    this.height = height;

    InitWindow(this.width, this.height, "Demo");
    SetTargetFPS(60);

    layerManager = new LayerManager();

    transparencyPatternRenderer = new SquarePatternRenderer();

    //    layerManager.addLayer(getTransparencyLayer());
    layerManager.addLayer(new PixelLayer(this.width, this.height));

    layerManager.addLayer(new PixelLayer(this.width, this.height));

    layerManager.setActiveLayer(0);

    previewManager =
        new LayerPreviewManager(new RaylibCanvas(this.width, this.height), BlendingMode.NORMAL);

    layerRenderer = new LayerRenderer(layerManager, previewManager, this.width, this.height);

    tools =
        new Tool[] {
          new Brush(layerManager, previewManager, operationManager, camera, 10),
          new Eraser(layerManager, previewManager, operationManager, camera, 10),
          new PanningTool(camera, operationManager),
          new MoveTool(layerManager, operationManager, camera)
        };

    selectedTool = tools[0];
  }

  private void render() {
    BeginDrawing();
    ClearBackground(
        DARKGRAY); // not necessary since the transparent pattern is writing over all pixels

    transparencyPatternRenderer.render(camera, width, height);

    layerRenderer.render(camera);

    DrawTexturePro(
        layerRenderer.getTexture(),
        new Rectangle().x(0).y(0).width(width).height(-height),
        new Rectangle().x(0).y(0).width(width).height(height),
        Vector2Zero(),
        0,
        WHITE);

    //    DrawTexture(layerRenderer.getTexture(), width, -height, WHITE);

    DrawFPS(20, 20);

    EndDrawing();
  }

  public void run() {
    while (!WindowShouldClose()) {
      render();

      Vector2 mousePosition = GetMousePosition();

      if (IsKeyDown(KEY_A)) {
        selectedTool = tools[0];
      } else if (IsKeyDown(KEY_W)) {
        selectedTool = tools[1];
      } else if (IsKeyDown(KEY_M)) {
        selectedTool = tools[2];
      } else if (IsKeyDown(KEY_V)) {
        selectedTool = tools[3];
      }

      if (IsMouseButtonDown(MOUSE_BUTTON_LEFT) && !mouseDown) {
        selectedTool.onPointerDown((int) mousePosition.x(), (int) mousePosition.y());
        mouseDown = true;
      }

      if (IsMouseButtonUp(MOUSE_BUTTON_LEFT) && mouseDown) {
        selectedTool.onPointerUp();
        mouseDown = false;
      }

      if (Vector2Distance(previousMousePosition, mousePosition) > 0.5f) {
        previousMousePosition = mousePosition;
        selectedTool.onPointerMove((int) mousePosition.x(), (int) mousePosition.y());
      }

      if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Z)) {
        operationManager.undo();
      }

      if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Y)) {
        operationManager.redo();
      }

      camera.zoomAt(
          width / 2f,
          height / 2f,
          Math.clamp(
              camera.getZoom() + GetMouseWheelMove() * 0.1f * camera.getZoom(), 0.2f, 1000.0f));
    }
  }
}
