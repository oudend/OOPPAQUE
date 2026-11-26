package se.chalmers.group7.core.elements.CanvasBlock;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import com.raylib.Raylib.RenderTexture;
import java.util.ArrayList;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.PixelLayer;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerRenderer;
import se.chalmers.group7.core.rendering.SquarePatternRenderer;
import se.chalmers.group7.core.tools.*;
import se.chalmers.group7.core.tools.move.MoveTool;
import se.chalmers.group7.core.tools.settings.ToolSettings;
import se.chalmers.group7.core.tools.stroke.Brush;
import se.chalmers.group7.core.tools.stroke.CommonStrokeSettings;
import se.chalmers.group7.core.tools.stroke.Eraser;
import se.chalmers.group7.core.ui.controller.Input;
import se.chalmers.group7.core.ui.model.LayoutBlock;

public class CanvasBlock extends LayoutBlock {
  private final RenderTexture compositeTexture;

  private final LayerManager layerManager;

  private final OperationManager operationManager = new OperationManager();
  private final ToolManager toolManager = new ToolManager();

  private final LayerRenderer layerRenderer;

  private final Camera camera;

  private final SquarePatternRenderer transparencyPatternRenderer;

  public CanvasBlock(int width, int height) {
    this.actions = new ArrayList<>();

    camera = new Camera(width, height);

    this.width = width;
    this.height = height;

    layerManager = new LayerManager();

    transparencyPatternRenderer = new SquarePatternRenderer();

    //    layerManager.addLayer(getTransparencyLayer());
    layerManager.addLayer(new PixelLayer(this.width, this.height));

    layerManager.addLayer(new PixelLayer(this.width, this.height));

    layerManager.setActiveLayer(0);

    layerRenderer = new LayerRenderer(layerManager, this.width, this.height);

    toolManager.addTool(new Brush(operationManager, layerManager, camera), "brush");
    toolManager.addTool(new Eraser(operationManager, layerManager, camera), "eraser");
    toolManager.addTool(new PanningTool(camera, operationManager), "pan");
    toolManager.addTool(new MoveTool(layerManager, operationManager, camera), "move");

    compositeTexture = LoadRenderTexture(width, height);
  }

  public ToolManager getToolManager() {
    return toolManager;
  }

  public void onInput(Input input) {
    if (IsKeyDown(KEY_A)) {
      toolManager.setActive("brush");
    } else if (IsKeyDown(KEY_W)) {
      toolManager.setActive("eraser");
    } else if (IsKeyDown(KEY_M)) {
      toolManager.setActive("pan");
    } else if (IsKeyDown(KEY_V)) {
      toolManager.setActive("move");
    }

    Tool activeTool = toolManager.getActiveTool();

    ToolSettings activeToolSettings = activeTool.getSettings();

    if (activeToolSettings != null) {
      float modifier = GetFrameTime();

      if (IsKeyDown(KEY_UP))
        activeToolSettings.updateSettingIfExists(
            CommonStrokeSettings.SIZE, size -> size + 5f * modifier);
      if (IsKeyDown(KEY_DOWN))
        activeToolSettings.updateSettingIfExists(
            CommonStrokeSettings.SIZE, size -> size - 5f * modifier);

      if (IsKeyDown(KEY_RIGHT))
        activeToolSettings.updateSettingIfExists(
            CommonStrokeSettings.OPACITY, opacity -> opacity + 0.05f * modifier);
      if (IsKeyDown(KEY_LEFT))
        activeToolSettings.updateSettingIfExists(
            CommonStrokeSettings.OPACITY, opacity -> opacity - 0.05f * modifier);
    }

    toolManager.onInput(input);

    if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Z)) {
      operationManager.undo();
    }

    if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Y)) {
      operationManager.redo();
    }
  }

  public void draw(int xPos, int yPos) {
    Raylib.Rectangle srcRect =
        new Raylib.Rectangle().x(0.0f).y(0.0f).width((float) width).height((float) -height);

    DrawTextureRec(compositeTexture.texture(), srcRect, new Vector2().x(xPos).y(yPos), WHITE);

    camera.zoomAt(
        width / 2f,
        height / 2f,
        Math.clamp(
            camera.getZoom() + GetMouseWheelMove() * 0.1f * camera.getZoom(), 0.2f, 1000.0f));
  }

  public void prepareComposite() {
    layerRenderer.render(camera, toolManager.getActiveLayerPreview());

    BeginTextureMode(compositeTexture);
    ClearBackground(
        DARKGRAY); // not necessary since the transparent pattern is writing over all pixels

    transparencyPatternRenderer.render(camera, width, height);

    DrawTexturePro(
        layerRenderer.getTexture(),
        new Rectangle().x(0).y(0).width(width).height(-height),
        new Rectangle().x(0).y(0).width(width).height(height),
        Vector2Zero(),
        0,
        WHITE);

    toolManager.renderOverlay(camera);

    EndTextureMode();
  }
}
