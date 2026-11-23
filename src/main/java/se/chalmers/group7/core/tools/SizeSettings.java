package se.chalmers.group7.core.tools;

public class SizeSettings {
  private int radius;

  public SizeSettings() {
    this(15);
  }

  public SizeSettings(int radius) {
    setRadius(radius);
  }

  public int getRadius() {
    return radius;
  }

  public void increase(int amount) {
    setRadius(this.radius + amount);
  }

  public void decrease(int amount) {
    setRadius(this.radius - amount);
  }

  private void setRadius(int radius) {
    this.radius = Math.max(1, radius);
  }
}
