package se.chalmers.group7.core.elements.CanvasBlock;

import static com.raylib.Colors.*;
import static com.raylib.Colors.GRAY;
import static com.raylib.Raylib.*;
import static se.chalmers.group7.core.canvas.utils.RaylibBlendModes.applyBlendMode;

import com.raylib.Raylib;
import com.raylib.Raylib.RenderTexture;
import java.util.ArrayList;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.temp.PreviewManager;
import se.chalmers.group7.core.tools.Brush;
import se.chalmers.group7.core.tools.Eraser;
import se.chalmers.group7.core.tools.Tool;
import se.chalmers.group7.core.ui.model.LayoutBlock;

public class CanvasBlock extends LayoutBlock {
  private Tool selectedTool;

  private final RaylibCanvas selectedLayer;

  private final RenderTexture compositeTexture;

  private final PreviewManager previewManager;
  private final RaylibCanvas previewLayer;

  private RenderTexture transparencyPattern;

  private final OperationManager operationManager = new OperationManager();

  private final Tool[] tools;

  public CanvasBlock(int width, int height, int xPos, int yPos) {
    this.actions = new ArrayList<>();

    this.width = width;
    this.height = height;
    this.xPos = xPos;
    this.yPos = yPos;

    selectedLayer = new RaylibCanvas(width, height);

    previewManager = new PreviewManager(new RaylibCanvas(width, height), BlendingMode.NORMAL);

    previewLayer = previewManager.getPreviewLayer();

    tools =
        new Tool[] {
          new Brush(selectedLayer, previewManager, operationManager, 20),
          new Eraser(selectedLayer, previewManager, operationManager, 20)
        };

    setSelectedTool(Brush.getInstance());

    compositeTexture = LoadRenderTexture(width, height);

    createTransparencyPattern();
  }

  public void draw(int xPos, int yPos) {
    Raylib.Rectangle srcRect =
        new Raylib.Rectangle()
            .x(0.0f)
            .y(0.0f)
            .width((float) selectedLayer.getWidth())
            .height((float) -selectedLayer.getHeight());

    // Renders transparent pattern
    applyBlendMode(BlendingMode.NORMAL);
    DrawTexturePro(
        transparencyPattern.texture(),
        new Rectangle().x(0).y(0).width(width / 20.0f * 15.0f).height(height / 20.0f * 15.0f),
        new Rectangle().x(xPos).y(yPos).width(width).height(height),
        Vector2Zero(),
        0,
        WHITE);
    EndBlendMode();

    DrawTextureRec(compositeTexture.texture(), srcRect, new Vector2().x(xPos).y(yPos), WHITE);
  }

  public void prepareComposite() {
    Raylib.Rectangle srcRect =
        new Raylib.Rectangle()
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
  }

  private void createTransparencyPattern() {
    transparencyPattern = LoadRenderTexture(20, 20);

    BeginTextureMode(transparencyPattern);

    ClearBackground(BLANK);

    DrawRectangle(0, 0, 10, 10, GRAY);
    DrawRectangle(10, 10, 10, 10, GRAY);

    EndTextureMode();

    SetTextureWrap(transparencyPattern.texture(), TEXTURE_WRAP_REPEAT);
  }

  public void setSelectedTool(Tool tool) {
    selectedTool = tool;
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
}
