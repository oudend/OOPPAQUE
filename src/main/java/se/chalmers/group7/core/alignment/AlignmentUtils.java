package se.chalmers.group7.core.alignment;

import se.chalmers.group7.core.layers.Layer;

public class AlignmentUtils {
  public static AlignmentLine[] fromRectangle(int x, int y, float angle) {
    return new AlignmentLine[] {
      new AlignmentLine(x, y, angle), new AlignmentLine(x, y, angle + 90)
    };
  }

  public static AlignmentLine[] fromLayer(Layer layer) {
    return fromRectangle(layer.getX(), layer.getY(), layer.getRotation());
  }

  private static float angularDifference(float a1, float a2) {
    float diff = Math.abs(a1 - a2) % 180;
    return Math.min(diff, 180 - diff);
  }

  private static float perpendicularDistance(AlignmentLine a, AlignmentLine b) {
    double angle = Math.toRadians(a.angle());

    float dx = b.x() - a.x();
    float dy = b.y() - a.y();

    return (float) Math.abs(-dx * Math.sin(angle) + dy * Math.cos(angle));
  }
}
