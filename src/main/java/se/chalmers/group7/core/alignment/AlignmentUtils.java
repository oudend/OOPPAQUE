package se.chalmers.group7.core.alignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerUtils;

public class AlignmentUtils {
  public static List<AlignmentLine> fromRectangle(float x, float y, float width, float height) {
    return List.of(
        new AlignmentLine[] {
          new AlignmentLine(x, y, AlignmentLineType.HORIZONTAL),
          new AlignmentLine(x, y, AlignmentLineType.VERTICAL),
          new AlignmentLine(x + width / 2, y + height / 2, AlignmentLineType.HORIZONTAL),
          new AlignmentLine(x + width / 2, y + height / 2, AlignmentLineType.VERTICAL),
          new AlignmentLine(x, y + height, AlignmentLineType.HORIZONTAL),
          new AlignmentLine(x + width, y, AlignmentLineType.VERTICAL),
        });
  }

  public static List<AlignmentLine> fromLayer(Layer layer) {
    LayerUtils.BoundingBox box = LayerUtils.getBoundingBox(layer);
    return fromRectangle(box.x(), box.y(), box.width(), box.height());
  }

  public static List<AlignmentLine> fromLayers(List<Layer> layers) {
    if (layers.isEmpty()) return Collections.emptyList();

    List<AlignmentLine> lines = new ArrayList<AlignmentLine>(fromLayer(layers.getFirst()));

    for (int i = 1; i < layers.size(); i++) {
      lines.addAll(fromLayer(layers.get(i)));
    }

    return lines;
  }

  public static float angularDifference(float a1, float a2) {
    float diff = Math.abs(a1 - a2) % 180;
    return Math.min(diff, 180 - diff);
  }

  public static float perpendicularDistance(AlignmentLine a, AlignmentLine b) {
    double angle = Math.toRadians(a.angle());

    float dx = b.x() - a.x();
    float dy = b.y() - a.y();

    return (float) Math.abs(-dx * Math.sin(angle) + dy * Math.cos(angle));
  }
}
