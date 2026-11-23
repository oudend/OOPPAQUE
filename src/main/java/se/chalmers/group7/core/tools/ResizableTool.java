package se.chalmers.group7.core.tools;

public interface ResizableTool {
  SizeSettings getSizeSettings();

  default void increaseSize(int amount) {
    getSizeSettings().increase(amount);
  }

  default void decreaseSize(int amount) {
    getSizeSettings().decrease(amount);
  }
}
