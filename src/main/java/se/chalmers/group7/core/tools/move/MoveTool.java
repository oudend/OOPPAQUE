package se.chalmers.group7.core.tools.move;

import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import se.chalmers.group7.core.alignment.AlignmentGuideRenderer;
import se.chalmers.group7.core.alignment.AlignmentLine;
import se.chalmers.group7.core.alignment.AlignmentUtils;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.operations.LayerTransformOperation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.CompositeScreenOverlay;
import se.chalmers.group7.core.rendering.LayerGuideRenderer;
import se.chalmers.group7.core.rendering.ScreenOverlay;
import se.chalmers.group7.core.tools.Tool;
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

  private Raylib.Vector2 layerEnterCoords;

  private final LayerGuideRenderer guideOverlay;
  private final AlignmentGuideRenderer alignmentOverlay;
  private final CompositeScreenOverlay screenOverlay;

  public MoveTool(LayerManager layerManager, OperationManager operationManager, Camera camera) {
    this.layerManager = layerManager;
    this.camera = camera;
    this.operationManager = operationManager;
    this.guideOverlay = new LayerGuideRenderer(this.layerManager);
    this.alignmentOverlay = new AlignmentGuideRenderer();
    this.screenOverlay = new CompositeScreenOverlay(List.of(guideOverlay, alignmentOverlay));
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

    activeLayerIndex = LayerUtils.layerIntersection(layerManager.getLayers(), camera, x, y);

    boolean intersection = activeLayerIndex != -1;

    if (!intersection) activeLayerIndex = layerManager.getActiveLayerIndex();

    layerManager.setActiveLayer(activeLayerIndex);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    layerEnterCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

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
  }

  private List<MoveToolUtils.LineMatch> getAlignmentLines() {
    List<Layer> comparisonLayers = new ArrayList<>(layerManager.getLayers());
    comparisonLayers.remove(layerManager.getActiveLayerIndex());

    List<AlignmentLine> comparisonLines = AlignmentUtils.fromLayers(comparisonLayers);
    List<AlignmentLine> workspaceLines =
        AlignmentUtils.fromRectangle(
            0, 0, camera.getViewportWidth(), camera.getViewportHeight(), 0);

    comparisonLines.addAll(workspaceLines);

    List<AlignmentLine> mainLines = AlignmentUtils.fromLayer(layerManager.getActiveLayer());

    return MoveToolUtils.findBestMatches(mainLines, comparisonLines, 5f, 0.001f);
  }

  public void onPointerMove(int x, int y) {
    if (activeLayerIndex == -1) return;

    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    List<MoveToolUtils.LineMatch> matches = getAlignmentLines();

    if (!matches.isEmpty()) {
      List<AlignmentLine> comparisonLines =
          matches.stream().map(MoveToolUtils.LineMatch::comparisonLine).toList();

      alignmentOverlay.setLines(comparisonLines);
    } else {
      alignmentOverlay.setLines(Collections.emptyList());
    }

    switch (transformType) {
      case MOVE:
        Raylib.Vector2 layerEnterWorld =
            Camera.layerCoordinatesToWorld(activeLayer, layerEnterCoords.x(), layerEnterCoords.y());

        Raylib.Vector2 mouseDelta = Vector2Subtract(worldVector, layerEnterWorld);

        Raylib.Vector2 mouseIdeal =
            Vector2Add(new Vector2().x(activeLayer.getX()).y(activeLayer.getY()), mouseDelta);

        if (!matches.isEmpty()) {
          AlignmentLine main = matches.getFirst().mainLine();
          AlignmentLine comparison = matches.getFirst().comparisonLine();

          Raylib.Vector2 alignmentDelta = AlignmentUtils.getOffsetVector(comparison, main);

          Raylib.Vector2 alignmentIdeal =
              Vector2Add(new Vector2().x(activeLayer.getX()).y(activeLayer.getY()), alignmentDelta);

          float angle = (float) Math.toRadians(main.angle());

          Raylib.Vector2 direction =
              new Vector2().x((float) Math.cos(angle)).y((float) Math.sin(angle));

          Raylib.Vector2 delta = Vector2Subtract(mouseIdeal, alignmentIdeal);

          float projection = delta.x() * direction.x() + delta.y() * direction.y();

          Raylib.Vector2 slidingPosition =
              Vector2Add(alignmentIdeal, Vector2Scale(direction, projection));

          float screenDistance =
              Vector2Distance(
                  camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y()),
                  camera.worldToScreen(slidingPosition.x(), slidingPosition.y()));

          if (screenDistance < 40f) {
            activeLayer.setX(Math.round(slidingPosition.x()));

            activeLayer.setY(Math.round(slidingPosition.y()));
            break;
          }
        }

        activeLayer.setX((int) mouseIdeal.x());

        activeLayer.setY((int) mouseIdeal.y());
        break;
      case RESIZE:
        MoveToolUtils.resizeLayer(camera, activeLayer, initialTransform, activeHandle, x, y);
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
    return Optional.of(screenOverlay);
  }
}
