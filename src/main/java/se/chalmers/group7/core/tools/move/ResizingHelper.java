package se.chalmers.group7.core.tools.move;

import static se.chalmers.group7.core.tools.move.ResizingUtils.snapPointToLayerLines;

import com.raylib.Raylib;
import java.util.ArrayList;
import java.util.List;
import se.chalmers.group7.core.alignment.AlignmentLine;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.rendering.Camera;

class ResizingHelper {
  private LayerTransform initialTransform;
  private LayerUtils.Handle activeHandle;
  private Layer activeLayer;

  private List<AlignmentLine> overlayLines = new ArrayList<>();

  void selectLayer(Layer layer, LayerUtils.Handle handle) {
    activeLayer = layer;
    activeHandle = handle;
    initialTransform = activeLayer.getLayerTransform();
  }

  void resize(Camera camera, List<Layer> layers, int x, int y) {
    overlayLines.clear();

    Raylib.Vector2 mouseWorld = camera.screenToWorld(x, y);

    mouseWorld =
        snapPointToLayerLines(
            camera,
            layers.stream().filter(l -> l != activeLayer).toList(),
            overlayLines,
            mouseWorld,
            10f);

    ResizingUtils.resizeLayer(
        camera, activeLayer, layers, initialTransform, activeHandle, mouseWorld);
  }

  List<AlignmentLine> getOverlayLines() {
    return overlayLines;
  }
}
