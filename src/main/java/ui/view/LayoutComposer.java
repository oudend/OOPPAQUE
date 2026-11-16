package ui.view;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import java.util.ArrayList;
import java.util.List;
import ui.model.LayoutLayer;

public class LayoutComposer {
  List<LayoutLayer> layers;

  public LayoutComposer() {
    layers = new ArrayList<>();
  }

  public void addLayer(LayoutLayer layer) {
    layers.add(layer);
  }

  public void removeLayer(LayoutLayer layer) {
    layers.remove(layer);
  }

  public void draw() {
    BeginDrawing();
    ClearBackground(WHITE);
    for (LayoutLayer l : layers) {
      l.draw();
    }

    DrawFPS(20, 20);
    EndDrawing();
  }
}
