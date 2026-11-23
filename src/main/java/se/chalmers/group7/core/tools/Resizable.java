package se.chalmers.group7.core.tools;

public interface Resizable {
  NumericSetting getSize();

  default void increaseSize(int amount) {
    getSize().increase(amount);
  }

  default void decreaseSize(int amount) {
    getSize().decrease(amount);
  }
}
