package se.chalmers.group7.core;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import static se.chalmers.group7.core.canvas.utils.RaylibBlendModes.applyBlendMode;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerPreviewManager;
import se.chalmers.group7.core.layers.PixelLayer;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerRenderer;
import se.chalmers.group7.core.tools.Brush;
import se.chalmers.group7.core.tools.Eraser;
import se.chalmers.group7.core.tools.Tool;

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

  private final Tool[] tools;
  private Camera camera = new Camera();

  public App(int width, int height) {
    this.width = width;
    this.height = height;

    InitWindow(this.width, this.height, "Demo");
    SetTargetFPS(6000);

    layerManager = new LayerManager();

    layerManager.addLayer(new PixelLayer(this.width, this.height));

    previewManager =
        new LayerPreviewManager(new RaylibCanvas(this.width, this.height), BlendingMode.NORMAL);

    layerRenderer = new LayerRenderer(layerManager, previewManager, this.width, this.height);

    tools =
        new Tool[] {
          new Brush(layerManager, previewManager, operationManager, camera, 20),
          new Eraser(layerManager, previewManager, operationManager, camera, 20)
        };

    selectedTool = tools[0];

    createTransparencyPattern();
  }

  private RenderTexture transparencyPattern;

  private void createTransparencyPattern() {
    transparencyPattern = LoadRenderTexture(20, 20);

    BeginTextureMode(transparencyPattern);

    ClearBackground(BLANK);

    DrawRectangle(0, 0, 10, 10, GRAY);
    DrawRectangle(10, 10, 10, 10, GRAY);

    EndTextureMode();

    SetTextureWrap(transparencyPattern.texture(), TEXTURE_WRAP_REPEAT);
  }

  private void render() {
    BeginDrawing();
    ClearBackground(
        RAYWHITE); // not necessary since the transparent pattern is writing over all pixels
    // anyway.

    // Renders transparent pattern
    applyBlendMode(BlendingMode.NORMAL);
    DrawTexturePro(
        transparencyPattern.texture(),
        new Rectangle().x(0).y(0).width(width / 20.0f * 15.0f).height(height / 20.0f * 15.0f),
        new Rectangle().x(0).y(0).width(width).height(height),
        Vector2Zero(),
        0,
        WHITE);
    EndBlendMode();

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

      if (IsKeyDown(KEY_W)) {
        selectedTool = tools[1];
      } else if (IsKeyUp(KEY_W)) {
        selectedTool = tools[0];
      }

      if (IsMouseButtonDown(MOUSE_BUTTON_LEFT) && !mouseDown) {
        selectedTool.onPointerDown((int) mousePosition.x(), (int) mousePosition.y());
        mouseDown = true;
      }

      if (IsMouseButtonUp(MOUSE_BUTTON_LEFT) && mouseDown) {
        selectedTool.onPointerUp();
        mouseDown = false;
      }

      if (Vector2Distance(previousMousePosition, mousePosition) > 1) {
        previousMousePosition = mousePosition;
        selectedTool.onPointerMove((int) mousePosition.x(), (int) mousePosition.y());
      }

      if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Z)) {
        operationManager.undo();
      }

      if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Y)) {
        operationManager.redo();
      }
    }
  }
}
