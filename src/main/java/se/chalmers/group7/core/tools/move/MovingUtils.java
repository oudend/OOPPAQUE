package se.chalmers.group7.core.tools.move;

import static com.raylib.Raylib.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import se.chalmers.group7.core.alignment.AlignmentLine;
import se.chalmers.group7.core.alignment.AlignmentLineType;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.rendering.Camera;

class MovingUtils {
  record MainLine(Layer layer, int index, float angle) {
    public AlignmentLine alignmentLine(Vector2 mouseIdeal) {

      LayerUtils.BoundingBox mainBox =
          LayerUtils.getBoundingBox(
              mouseIdeal.x(),
              mouseIdeal.y(),
              layer.getWidth(),
              layer.getHeight(),
              layer.getRotation());

      List<AlignmentLine> mainLines =
          se.chalmers.group7.core.alignment.AlignmentUtils.fromRectangle(
              mainBox.x(), mainBox.y(), mainBox.width(), mainBox.height());

      return mainLines.get(index);
    }
  }

  record LineMatch(MainLine mainLine, AlignmentLine comparisonLine, AlignmentLineType type) {}

  public static Vector2 getSlidingPosition(Vector2 mouseIdeal, LineMatch match) {
    AlignmentLine main = match.mainLine().alignmentLine(mouseIdeal);
    AlignmentLine comparison = match.comparisonLine();

    double angle = Math.toRadians(main.angle());
    float nx = (float) -Math.sin(angle);
    float ny = (float) Math.cos(angle);

    float dx = comparison.x() - main.x();
    float dy = comparison.y() - main.y();

    // Signed distance along normal from main -> comparison
    float signedPerpDist = dx * nx + dy * ny;

    return new Vector2()
        .x(mouseIdeal.x() + nx * signedPerpDist)
        .y(mouseIdeal.y() + ny * signedPerpDist);
  }

  public static Vector2 getIntersectionPosition(
      Vector2 mouseIdeal, LineMatch first, LineMatch second) {
    Vector2 p1 = getSlidingPosition(mouseIdeal, first);
    Vector2 p2 = getSlidingPosition(mouseIdeal, second);

    AlignmentLine line1 = first.mainLine().alignmentLine(mouseIdeal);
    AlignmentLine line2 = second.mainLine().alignmentLine(mouseIdeal);

    double a1 = Math.toRadians(line1.angle());
    double a2 = Math.toRadians(line2.angle());

    // Direction vectors of the two lines
    double d1x = Math.cos(a1);
    double d1y = Math.sin(a1);

    double d2x = Math.cos(a2);
    double d2y = Math.sin(a2);

    // Solve p1 + t*d1 = p2 + u*d2
    double denominator = d1x * d2y - d1y * d2x;

    if (Math.abs(denominator) < 1e-9) {
      return null; // Parallel lines: no unique intersection
    }

    double dx = p2.x() - p1.x();
    double dy = p2.y() - p1.y();

    double t = (dx * d2y - dy * d2x) / denominator;

    return new Vector2().x((float) (p1.x() + t * d1x)).y((float) (p1.y() + t * d1y));
  }

  public static List<LineMatch> findCloseMatches(
      Camera camera, List<MovingUtils.LineMatch> matches, Vector2 mouseIdeal, float minDistance) {

    return matches.stream()
        .filter(match -> {
          Vector2 slidingPosition = getSlidingPosition(mouseIdeal, (LineMatch) match);

          Vector2 mouseScreen = camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y());

          Vector2 slidingScreen = camera.worldToScreen(slidingPosition.x(), slidingPosition.y());

          return Vector2Distance(mouseScreen, slidingScreen) < minDistance;
        })
        .toList();
  }

  public record AlignedLineMatches(LineMatch closest, LineMatch other) {}

  public static AlignedLineMatches alignedLineMatches(
      Camera camera, List<LineMatch> matches, Vector2 mouseIdeal) {

    LineMatch horizontal =
        closestMatchToMouse(
            camera,
            matches.stream().filter(match -> match.type() == AlignmentLineType.HORIZONTAL),
            mouseIdeal);

    LineMatch vertical =
        closestMatchToMouse(
            camera,
            matches.stream().filter(match -> match.type() == AlignmentLineType.VERTICAL),
            mouseIdeal);

    if (horizontal == null) {
      return new AlignedLineMatches(vertical, null);
    }

    if (vertical == null) {
      return new AlignedLineMatches(horizontal, null);
    }

    double horizontalDistance = getDistance(camera, horizontal, mouseIdeal);
    double verticalDistance = getDistance(camera, vertical, mouseIdeal);

    return horizontalDistance <= verticalDistance
        ? new AlignedLineMatches(horizontal, vertical)
        : new AlignedLineMatches(vertical, horizontal);
  }

  public static LineMatch closestMatchToMouse(
      Camera camera, Stream<LineMatch> matches, Vector2 mouseIdeal) {

    return matches
        .min(Comparator.comparingDouble(match -> getDistance(camera, match, mouseIdeal)))
        .orElse(null);
  }

  private static double getDistance(Camera camera, LineMatch match, Vector2 mouseIdeal) {

    Vector2 slidingPosition = getSlidingPosition(mouseIdeal, match);

    Vector2 mouseScreen = camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y());

    Vector2 slidingScreen = camera.worldToScreen(slidingPosition.x(), slidingPosition.y());

    return Vector2Distance(mouseScreen, slidingScreen);
  }

  public static List<LineMatch> getAllAlignmentMatches(
      Layer mainLayer, List<AlignmentLine> comparisonLines, Vector2 mouseIdeal) {
    List<LineMatch> matches = new ArrayList<>();

    LayerUtils.BoundingBox mainBox =
        LayerUtils.getBoundingBox(
            mouseIdeal.x(),
            mouseIdeal.y(),
            mainLayer.getWidth(),
            mainLayer.getHeight(),
            mainLayer.getRotation());

    List<AlignmentLine> mainLines =
        se.chalmers.group7.core.alignment.AlignmentUtils.fromRectangle(
            mainBox.x(), mainBox.y(), mainBox.width(), mainBox.height());

    for (int i = 0; i < mainLines.size(); i++) {
      AlignmentLine mLine = mainLines.get(i);

      for (AlignmentLine cLine : comparisonLines) {

        if (mLine.type() != cLine.type()) {
          continue;
        }

        MainLine mainLine = new MainLine(mainLayer, i, mLine.angle());

        switch (mLine.type()) {
          case HORIZONTAL, VERTICAL -> matches.add(new LineMatch(mainLine, cLine, mLine.type()));
        }
      }
    }

    return matches;
  }
}
