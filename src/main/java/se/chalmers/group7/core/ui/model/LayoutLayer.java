package se.chalmers.group7.core.ui.model;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class LayoutLayer extends LayoutBlock {
  int xPos;
  int yPos;

  RenderTexture canvas;
  LayoutBlock root;

  public LayoutLayer(LayoutBlock root, int xPos, int yPos, int width, int height) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.width = width;
    this.height = height;

    this.canvas = LoadRenderTexture(width, height);
    this.root = root;
  }

  @Override
  public void draw() {
    draw(xPos, yPos);
  }

  public void draw(int x, int y) {
    BeginTextureMode(canvas);
    root.draw();
    EndTextureMode();

    Rectangle rect = new Rectangle().x(x).y(y).width(width).height(-height);
    DrawTextureRec(canvas.texture(), rect, Vector2Zero(), WHITE);
  }
}
