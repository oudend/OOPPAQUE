package se.chalmers.group7.core.alignment;

import com.raylib.Raylib;
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

  public static AlignmentLine closestLine(Raylib.Vector2 point, List<AlignmentLine> lines) {
    float closestScreenDistance = 0f;
    //    Raylib.Vector2 closestPoint = null;
    AlignmentLine closestLine = null;

    for (AlignmentLine alignmentLine : lines) {
      Raylib.Vector2 p = AlignmentUtils.closestPointOnLine(point, alignmentLine);

      float screenDistance = Raylib.Vector2Distance(p, point);

      if (closestLine == null || screenDistance < closestScreenDistance) {
        closestLine = alignmentLine;
        closestScreenDistance = screenDistance;
      }
    }

    return closestLine;
  }

  public static Raylib.Vector2 closestPointOnLine(Raylib.Vector2 point, AlignmentLine line) {
    double angle = Math.toRadians(line.angle());

    // Direction vector of the line
    float dx = (float) Math.cos(angle);
    float dy = (float) Math.sin(angle);

    // Vector from line origin to point
    float px = point.x() - line.x();
    float py = point.y() - line.y();

    // Project onto direction vector
    float t = px * dx + py * dy;

    return new Raylib.Vector2().x(line.x() + t * dx).y(line.y() + t * dy);
  }
}
