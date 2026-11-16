package ui.model;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import com.raylib.Raylib.*;

public class LayoutLayer {
  int xPos;
  int yPos;
  int width;
  int height;

  RenderTexture canvas;
  LayoutBlock root;

  public void draw() {
    BeginTextureMode(canvas);
    root.draw();
    EndTextureMode();

    Rectangle rect = new Rectangle().x(xPos).y(yPos).width(width).height(-height);
    DrawTextureRec(canvas.texture(), rect, Vector2Zero(), WHITE);
  }
}
