package se.chalmers.group7.core.tools;

// mouse events?
// void onMouseEvent(MouseEvent);
// void onSelect();
// void onDeactivate();

public interface Tool {
  void onPointerDown(int x, int y);

  void onPointerMove(int x, int y);

  void onPointerUp();

  void deactivate();
}
