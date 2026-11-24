package se.chalmers.group7.core.alignment;

import com.raylib.Raylib;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerUtils;

public class AlignmentUtils {
  public static List<AlignmentLine> fromRectangle(
      float x, float y, float width, float height, float angle) {
    return List.of(
        new AlignmentLine[] {
          new AlignmentLine(x, y, angle),
          new AlignmentLine(x, y, angle + 90),
          new AlignmentLine(x + width / 2, y + height / 2, angle),
          new AlignmentLine(x + width / 2, y + height / 2, angle + 90),
          new AlignmentLine(x, y + height, angle),
          new AlignmentLine(x + width, y, angle + 90),
        });
  }

  public static List<AlignmentLine> fromLayer(Layer layer) {
    LayerUtils.BoundingBox box = LayerUtils.getBoundingBox(layer);
    return fromRectangle(box.x(), box.y(), box.width(), box.height(), 0f);
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

  public static Raylib.Vector2 getOffsetVector(AlignmentLine a, AlignmentLine b) {
    double angle = Math.toRadians(a.angle());

    // Unit normal vector (perpendicular to the lines)
    double nx = -Math.sin(angle);
    double ny = Math.cos(angle);

    // Difference between points on the two lines
    double dx = b.x() - a.x();
    double dy = b.y() - a.y();

    // Project onto the normal to get the shortest displacement
    double distance = dx * nx + dy * ny;

    return new Raylib.Vector2().x((float) (nx * distance)).y((float) (ny * distance));
  }

  public static boolean compareAlignment(
      AlignmentLine a, AlignmentLine b, float aDiff, float pDist) {
    return angularDifference(a.angle(), b.angle()) < aDiff && perpendicularDistance(a, b) < pDist;
  }
}
