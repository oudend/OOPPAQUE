package se.chalmers.group7.core.elements.CanvasBlock;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import com.raylib.Raylib.RenderTexture;
import java.util.ArrayList;
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
import se.chalmers.group7.core.ui.model.LayoutBlock;

public class CanvasBlock extends LayoutBlock {
  private Tool selectedTool;

  private final RenderTexture compositeTexture;

  private final LayerPreviewManager previewManager;

  private RenderTexture transparencyPattern;

  private final LayerManager layerManager;
  private final OperationManager operationManager = new OperationManager();

  private final LayerRenderer layerRenderer;

  private final Tool[] tools;

  private final Camera camera = new Camera();

  private final SquarePatternRenderer transparencyPatternRenderer;

  public CanvasBlock(int width, int height) {
    this.actions = new ArrayList<>();

    this.width = width;
    this.height = height;

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

    setSelectedTool(0);

    compositeTexture = LoadRenderTexture(width, height);
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
    layerRenderer.render(camera);

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

    EndTextureMode();
  }

  public void setSelectedTool(int toolIndex) {
    selectedTool = tools[toolIndex];
  }

  public RenderTexture getTransparencyPattern() {
    return transparencyPattern;
  }

  public Tool getSelectedTool() {
    return selectedTool;
  }

  public OperationManager getOperationManager() {
    return operationManager;
  }

  public Brush getBrush() {
    return (Brush) tools[0];
  }
}
