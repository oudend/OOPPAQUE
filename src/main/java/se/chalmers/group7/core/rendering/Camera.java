package se.chalmers.group7.core.rendering;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;

public class Camera {
  private float x;
  private float y;
  private float zoom;

  public Camera(float x, float y, float zoom) {
    this.x = x;
    this.y = y;
    this.zoom = zoom;
  }

  public Camera(float x, float y) {
    this(x, y, 1.0f);
  }

  public Camera() {
    this(0, 0, 1);
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void zoomAt(float screenX, float screenY, float newZoom) {
    // World position underneath the chosen screen point before zooming.
    float worldX = x + screenX / zoom;
    float worldY = y + screenY / zoom;

    setZoom(newZoom);

    // Update camera position to preserve the world point.
    x = worldX - screenX / zoom;
    y = worldY - screenY / zoom;
  }

  public float getZoom() {
    return zoom;
  }

  public void setZoom(float zoom) {
    this.zoom = Math.max(0.0f, zoom);
  }

  public Raylib.Vector2 screenToWorld(float screenX, float screenY) {
    return new Raylib.Vector2().x(x + screenX / zoom).y(y + screenY / zoom);
  }

  public Raylib.Vector2 worldToScreen(float worldX, float worldY) {
    return new Raylib.Vector2().x((worldX - x) * zoom).y((worldY - y) * zoom);
  }

  public Raylib.Vector2 screenToLayerCoordinates(Layer layer, float screenX, float screenY) {
    Raylib.Vector2 worldPosition = screenToWorld(screenX, screenY);

    return worldToLayerCoordinates(layer, worldPosition.x(), worldPosition.y());
  }

  public static Raylib.Vector2 worldToLayerCoordinates(Layer layer, float worldX, float worldY) {
    // Translate into layer-relative coordinates.
    float dx = worldX - layer.getX() - layer.getWidth() / 2.0f;
    float dy = worldY - layer.getY() - layer.getHeight() / 2.0f;

    // Undo the layer rotation.
    double radians = Math.toRadians(layer.getRotation());

    float cos = (float) Math.cos(radians);
    float sin = (float) Math.sin(radians);

    float localX = dx * cos + dy * sin;
    float localY = -dx * sin + dy * cos;

    // Move origin from center to top-left.
    localX += layer.getWidth() / 2.0f;
    localY += layer.getHeight() / 2.0f;

    // Convert workspace coordinates to canvas pixel coordinates.
    localX *= layer.getCanvas().getWidth() / (float) layer.getWidth();
    localY *= layer.getCanvas().getHeight() / (float) layer.getHeight();

    return new Raylib.Vector2().x(localX).y(localY);
  }
}
