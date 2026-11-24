package se.chalmers.group7.core.tools;

public interface Resizable {
  NumericSetting getSize();

  default void increaseSize(float amount) {
    getSize().increase(amount);
  }

  default void decreaseSize(float amount) {
    getSize().decrease(amount);
  }
}
