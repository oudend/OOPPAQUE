package se.chalmers.group7.core.tools;

import static com.raylib.Raylib.GetFrameTime;

import com.raylib.Raylib;
import java.util.Optional;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.operations.LayerTransformOperation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerGuideRenderer;
import se.chalmers.group7.core.rendering.ScreenOverlay;
import se.chalmers.group7.core.ui.controller.Input;

public class MoveTool implements Tool {
  private enum TransformType {
    MOVE,
    RESIZE,
    ROTATE
  }

  private OperationManager operationManager;
  private final LayerManager layerManager;
  private final Camera camera;

  private int activeLayerIndex = -1;
  private TransformType transformType;

  private LayerTransform initialTransform;

  private LayerUtils.Handle activeHandle;

  private Integer previousX;
  private Integer previousY;

  private final LayerGuideRenderer screenOverlay;

  public MoveTool(LayerManager layerManager, OperationManager operationManager, Camera camera) {
    this.layerManager = layerManager;
    this.camera = camera;
    this.operationManager = operationManager;
    this.screenOverlay = new LayerGuideRenderer(this.layerManager);
  }

  private int layerIntersection(int x, int y) {
    // input: global coords.

    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);

    for (int i = 0; i < layerManager.getLayerCount(); i++) {
      Layer layer = layerManager.getLayer(i);

      boolean intersects =
          (worldVector.x() >= layer.getX() && worldVector.x() <= layer.getX() + layer.getWidth())
              && (worldVector.y() >= layer.getY()
                  && worldVector.y() <= layer.getY() + layer.getHeight());

      if (intersects) {
        return i;
      }
    }

    return -1;
  }

  @Override
  public void onInput(Input input) {
    if (input.mouseLeft().down()) {
      onPointerDown(input.xPos(), input.yPos());
    }

    if (input.mouseLeft().released()) {
      onPointerUp();
    }

    if ((input.deltaX() * input.deltaX() + input.deltaY() * input.deltaY()) > GetFrameTime()) {
      onPointerMove(input.xPos(), input.yPos());
    }
  }

  public void onPointerDown(int x, int y) {
    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);
    Raylib.Vector2 screenVector = new Raylib.Vector2().x(x).y(y);

    activeLayerIndex = layerIntersection(x, y);

    boolean intersection = activeLayerIndex != -1;

    if (!intersection) activeLayerIndex = layerManager.getActiveLayerIndex();

    layerManager.setActiveLayer(activeLayerIndex);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    LayerUtils.Handle nearestHandle =
        LayerUtils.getNearestHandle(activeLayer, camera, worldVector.x(), worldVector.y());

    Raylib.Vector2 handleScreenCoords =
        camera.worldToScreen(nearestHandle.worldCoords().x(), nearestHandle.worldCoords().y());

    float handleDistance = Raylib.Vector2Distance(handleScreenCoords, screenVector);

    if (!intersection && handleDistance > 6f) {
      reset();
      return;
    }

    transformType = TransformType.MOVE;

    if (handleDistance <= 6f) {
      switch (nearestHandle.type()) {
        case ROTATION -> transformType = TransformType.ROTATE;
        case SCALE_CORNER, SCALE_WIDTH, SCALE_HEIGHT -> transformType = TransformType.RESIZE;
      }
    }

    if (transformType != TransformType.MOVE) activeHandle = nearestHandle;
    initialTransform = activeLayer.getLayerTransform();

    previousX = x;
    previousY = y;
  }

  public void onPointerMove(int x, int y) {
    if (activeLayerIndex == -1) return;

    Raylib.Vector2 screenVector = new Raylib.Vector2().x(x).y(y);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    float deltaX;
    float deltaY;

    switch (transformType) {
      case MOVE:
        deltaX = ((x - previousX) / camera.getZoom());
        deltaY = ((y - previousY) / camera.getZoom());

        activeLayer.setX((int) (activeLayer.getX() + deltaX));
        activeLayer.setY((int) (activeLayer.getY() + deltaY));
        break;
      case RESIZE:
        resizeLayer(activeLayer, x, y);
        break;
      case ROTATE:
        float centerX = activeLayer.getX() + activeLayer.getWidth() / 2f;
        float centerY = activeLayer.getY() + activeLayer.getHeight() / 2f;

        Raylib.Vector2 mouseWorld = camera.screenToWorld(x, y);

        float angle =
            (float) Math.toDegrees(Math.atan2(mouseWorld.y() - centerY, mouseWorld.x() - centerX))
                + 90f;

        activeLayer.setRotation(angle);
        break;
    }

    previousX = x;
    previousY = y;
  }

  public void onPointerUp() {
    if (activeLayerIndex == -1) return;

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    LayerTransform finalTransform = LayerTransform.from(activeLayer);

    if (!initialTransform.equals(finalTransform)) {
      LayerTransformOperation operation =
          new LayerTransformOperation(activeLayer, initialTransform, finalTransform, true);

      operationManager.addOperation(operation);
    }

    reset();
  }

  @Override
  public void deactivate() {
    reset();
  }

  private void reset() {
    transformType = null;
    activeHandle = null;
    activeLayerIndex = -1;
  }

  @Override
  public void onSelected() {}

  @Override
  public Optional<ScreenOverlay> getScreenOverlay() {
    if (layerManager.getLayerCount() == 0) return Optional.empty();

    return Optional.of(screenOverlay);
  }

  // mostly ai assisted math
  private void resizeLayer(Layer layer, int mouseX, int mouseY) {
    Raylib.Vector2 mouseWorld = camera.screenToWorld(mouseX, mouseY);
    Raylib.Vector2 handleWorld = activeHandle.worldCoords();

    float initX = initialTransform.x();
    float initY = initialTransform.y();
    float initW = initialTransform.width();
    float initH = initialTransform.height();

    // Rotation pivot (center of the layer)
    float pivotX = initX + initW / 2f;
    float pivotY = initY + initH / 2f;

    double angleRad = Math.toRadians(layer.getRotation());
    float cos = (float) Math.cos(angleRad);
    float sin = (float) Math.sin(angleRad);

    // Transform world coordinates into the layer's local space (inverse rotation)
    float mdx = mouseWorld.x() - pivotX;
    float mdy = mouseWorld.y() - pivotY;
    float mouseLocalX = mdx * cos + mdy * sin;
    float mouseLocalY = -mdx * sin + mdy * cos;

    float hdx = handleWorld.x() - pivotX;
    float hdy = handleWorld.y() - pivotY;
    float handleLocalX = hdx * cos + hdy * sin;
    float handleLocalY = -hdx * sin + hdy * cos;

    // Initial bounds in local space (relative to the center pivot)
    float left = -initW / 2f;
    float right = initW / 2f;
    float top = -initH / 2f;
    float bottom = initH / 2f;

    boolean moveLeft = Math.abs(handleLocalX - left) < Math.abs(handleLocalX - right);
    boolean moveTop = Math.abs(handleLocalY - top) < Math.abs(handleLocalY - bottom);

    float minWidth = 10f;
    float minHeight = 10f;

    switch (activeHandle.type()) {
      case SCALE_WIDTH, SCALE_CORNER -> {
        if (moveLeft) {
          left = Math.min(mouseLocalX, right - minWidth);
        } else {
          right = Math.max(mouseLocalX, left + minWidth);
        }
      }
      default -> {}
    }

    switch (activeHandle.type()) {
      case SCALE_HEIGHT, SCALE_CORNER -> {
        if (moveTop) {
          top = Math.min(mouseLocalY, bottom - minHeight);
        } else {
          bottom = Math.max(mouseLocalY, top + minHeight);
        }
      }
      default -> {}
    }

    float newWidth = right - left;
    float newHeight = bottom - top;

    // Find the new center in local space and rotate it back to world space
    float localCenterX = (left + right) / 2f;
    float localCenterY = (top + bottom) / 2f;
    float newCenterX = pivotX + (localCenterX * cos - localCenterY * sin);
    float newCenterY = pivotY + (localCenterX * sin + localCenterY * cos);

    layer.setX(Math.round(newCenterX - newWidth / 2f));
    layer.setY(Math.round(newCenterY - newHeight / 2f));
    layer.setWidth(Math.round(newWidth));
    layer.setHeight(Math.round(newHeight));
  }
}
