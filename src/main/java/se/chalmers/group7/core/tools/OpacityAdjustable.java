package se.chalmers.group7.core.tools;

public interface OpacityAdjustable {
  NumericSetting getOpacity();

  default void increaseOpacity(float amount) {
    getOpacity().increase(amount);
  }

  default void decreaseOpacity(float amount) {
    getOpacity().decrease(amount);
  }
}
