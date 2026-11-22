package se.chalmers.group7.core.elements.CanvasBlock;

import se.chalmers.group7.core.ui.model.MouseAction;
import com.raylib.Raylib.Color;

public class AChangeColor extends MouseAction {
    private final CanvasBlock canvas;
    private final Color color;

    public AChangeColor(CanvasBlock canvas, Color color) {
        this.canvas = canvas;
        this.color = color;
    }

    @Override
    public void onMouseLeftDown() {
        canvas.getBrush().setDrawColor(color);
    }
}
