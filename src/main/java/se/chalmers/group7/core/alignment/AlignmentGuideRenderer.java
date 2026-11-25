package se.chalmers.group7.core.alignment;

import static com.raylib.Colors.*;

import com.raylib.Raylib;
import java.util.ArrayList;
import java.util.List;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.overlays.ScreenOverlay;

public class AlignmentGuideRenderer implements ScreenOverlay {
  private List<AlignmentLine> lines = new ArrayList<>();

  public AlignmentGuideRenderer() {}

  public void setLines(List<AlignmentLine> lines) {
    this.lines = new ArrayList<>(lines);
  }

  public List<AlignmentLine> getLines() {
    return lines;
  }

  private void renderLine(Camera camera, AlignmentLine line) {
    int projectionRadius = Math.max(camera.getViewportWidth(), camera.getViewportHeight());

    Raylib.Vector2 c =
        new Raylib.Vector2().x(camera.getViewportWidth() / 2f).y(camera.getViewportHeight() / 2f);

    Raylib.Vector2 l = camera.worldToScreen(line.x(), line.y());

    double angle = Math.toRadians(line.angle());

    // Direction vector of the line
    double dx = Math.cos(angle);
    double dy = Math.sin(angle);

    // Vector from circle center to line point
    double fx = l.x() - c.x();
    double fy = l.y() - c.y();

    // Quadratic coefficients
    double a = dx * dx + dy * dy;
    double b = 2 * (fx * dx + fy * dy);
    double c1 = fx * fx + fy * fy - projectionRadius * projectionRadius;

    // Discriminant
    double discriminant = b * b - 4 * a * c1;

    // No intersection (or only one tangent intersection)
    if (discriminant <= 0) {
      return;
    }

    double sqrtD = Math.sqrt(discriminant);

    // Two intersection distances along the line
    double t1 = (-b - sqrtD) / (2 * a);
    double t2 = (-b + sqrtD) / (2 * a);

    // Intersection points
    double x1 = l.x() + t1 * dx;
    double y1 = l.y() + t1 * dy;

    double x2 = l.x() + t2 * dx;
    double y2 = l.y() + t2 * dy;

    Raylib.Color color =
        Raylib.ColorLerp(GREEN, RED, 1f - Math.abs((line.angle() % 180f) / 90f - 1f));

    Raylib.DrawLine((int) x1, (int) y1, (int) x2, (int) y2, color);
  }

  @Override
  public void render(Camera camera) {
    if (lines.isEmpty()) return;

    for (AlignmentLine line : lines) {
      renderLine(camera, line);
    }
  }
}
