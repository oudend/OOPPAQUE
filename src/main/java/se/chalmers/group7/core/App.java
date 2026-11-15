package se.chalmers.group7.core;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import static se.chalmers.group7.core.canvas.utils.RaylibBlendModes.applyBlendMode;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.temp.PreviewManager;
import se.chalmers.group7.core.tools.Brush;
import se.chalmers.group7.core.tools.Eraser;
import se.chalmers.group7.core.tools.Tool;

public class App {

  private int width;
  private int height;

  private Tool selectedTool;

  private RaylibCanvas selectedLayer;

  private RenderTexture compositeTexture;

  private PreviewManager previewManager;
  private RaylibCanvas previewLayer;

  private boolean mouseDown = false;

  private Vector2 previousMousePosition = Vector2Zero();

  private OperationManager operationManager = new OperationManager();

  private final Tool[] tools;

  public App(int width, int height) {
    this.width = width;
    this.height = height;

    InitWindow(this.width, this.height, "Demo");
    SetTargetFPS(6000);

    selectedLayer = new RaylibCanvas(this.width, this.height);

    previewManager =
        new PreviewManager(new RaylibCanvas(this.width, this.height), BlendingMode.NORMAL);

    previewLayer = previewManager.getPreviewLayer();

    tools =
        new Tool[] {
          new Brush(selectedLayer, previewManager, operationManager, 20),
          new Eraser(selectedLayer, previewManager, operationManager, 20)
        };

    selectedTool = tools[0];

    compositeTexture = LoadRenderTexture(this.width, this.height);

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

  private void renderComposite() {
    Rectangle srcRect =
        new Rectangle()
            .x(0.0f)
            .y(0.0f)
            .width((float) selectedLayer.getWidth())
            .height((float) -selectedLayer.getHeight());

    BeginTextureMode(compositeTexture); // your pre-allocated scratch RenderTexture
    ClearBackground(BLANK);

    // Exact copy of selectedLayer (RGB + Alpha) using your existing REPLACE mode
    applyBlendMode(BlendingMode.REPLACE);
    DrawTextureRec(selectedLayer.getTexture(), srcRect, Vector2Zero(), WHITE);
    EndBlendMode();

    // Now apply previewLayer onto the layer's alpha!
    Rectangle previewRect =
        new Rectangle()
            .x(0.0f)
            .y(0.0f)
            .width((float) previewLayer.getWidth())
            .height((float) -previewLayer.getHeight());
    applyBlendMode(previewManager.getPreviewBlending());
    DrawTextureRec(previewLayer.getTexture(), previewRect, Vector2Zero(), WHITE);
    EndBlendMode();
    EndTextureMode();

    DrawTextureRec(compositeTexture.texture(), srcRect, Vector2Zero(), WHITE);
  }

  public void run() {
    while (!WindowShouldClose()) {
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

      if (IsKeyDown(KEY_M)) {
        rlCheckErrors();
      }

      if (!IsKeyDown(KEY_K)) renderComposite();

      if (IsKeyDown(KEY_M)) {
        System.out.println(IsTextureValid(transparencyPattern.texture()));

        rlCheckErrors();
      }

      DrawFPS(20, 20);

      EndDrawing();

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
