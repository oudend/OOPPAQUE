package org.example;

import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.BLUE;
import static com.raylib.Colors.GRAY;
import static com.raylib.Colors.GREEN;
import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginTextureMode;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawCircleV;
import static com.raylib.Raylib.DrawFPS;
import static com.raylib.Raylib.DrawLineEx;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawTextureRec;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.EndTextureMode;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.GuiButton;
import static com.raylib.Raylib.GuiSlider;
import static com.raylib.Raylib.InitWindow;
import static com.raylib.Raylib.IsKeyDown;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.IsMouseButtonUp;
import static com.raylib.Raylib.KEY_LEFT_SHIFT;
import static com.raylib.Raylib.LoadRenderTexture;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;
import static com.raylib.Raylib.UnloadRenderTexture;
import static com.raylib.Raylib.Vector2Distance;
import static com.raylib.Raylib.Vector2Zero;
import static com.raylib.Raylib.WindowShouldClose;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.raylib.Raylib.Color;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.RenderTexture;
import com.raylib.Raylib.Vector2;

public class Main {

    static FloatBuffer floatBox(float initial) {
        FloatBuffer b = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        b.put(0, initial);
        return b;
    }

    public static void main(String args[]) {
        int screenWidth = 1280, screenHeight = 720;
        // int scale = 10;
        InitWindow(screenWidth, screenHeight, "OOPaque");

        int drawRadius = 10;

        Color selectedColor = BLACK;

        Color eraseColor = RAYWHITE; // ColorAlpha(BLACK, 0.0f);

        Color currentColor = selectedColor;

        Vector2 previousPosition = null;

        Color[] availableColors = { BLACK, RAYWHITE, RED, BLUE, GREEN };

        RenderTexture canvas = LoadRenderTexture(screenWidth, screenHeight);
        // SetTextureFilter(canvas, TEXTURE_FILTER_POINT);

        // SetTargetFPS(60);

        RadialColorPicker picker = new RadialColorPicker(450, 300, 200);
        picker.setColor(255, 120, 40);

        while (!WindowShouldClose()) {

            if (picker.getColor() != selectedColor) {
                selectedColor = picker.getColor();
            }

            if (IsKeyDown(KEY_LEFT_SHIFT)) {
                currentColor = eraseColor;
            } else {
                currentColor = selectedColor;
            }

            if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
                Vector2 currentPosition = GetMousePosition();

                if (previousPosition == null)
                    previousPosition = currentPosition;

                if (Vector2Distance(currentPosition, previousPosition) < drawRadius / 2.0f) {
                    BeginTextureMode(canvas);
                    DrawCircleV(currentPosition, drawRadius, currentColor);
                    EndTextureMode();
                } else {
                    BeginTextureMode(canvas);
                    DrawCircleV(previousPosition, drawRadius, currentColor);
                    DrawCircleV(GetMousePosition(), drawRadius, currentColor);
                    DrawLineEx(previousPosition, currentPosition, drawRadius * 2.0f, currentColor);
                    EndTextureMode();
                }

                previousPosition = currentPosition;
            }

            if (IsMouseButtonUp(MOUSE_BUTTON_LEFT))
                previousPosition = null;

            BeginDrawing();
            ClearBackground(RAYWHITE);
            Rectangle rect = new Rectangle()
                    .x(0.0f)
                    .y(0.0f)
                    .width((float) canvas.texture().width())
                    .height((float) -canvas.texture().height());
            DrawTextureRec(canvas.texture(), rect, Vector2Zero(), WHITE);
            // DrawTexture(canvas.texture(), 0, 0, WHITE);

            // Drawing the top bar
            DrawRectangle(0, 0, screenWidth, 125, WHITE);
            DrawRectangle(0, 125, screenWidth, 5, BLACK);

            // Drawing the tool elements
            int offset = 10;
            int colorWidth = 50;
            for (Color col : availableColors) {
                Color outlineColor = GRAY;
                if (col == currentColor) {
                    outlineColor = BLACK;
                }
                DrawRectangle(offset - 5, 5, colorWidth + 10, colorWidth + 10, outlineColor);
                DrawRectangle(offset, 10, colorWidth, colorWidth, col);

                offset += 20 + colorWidth;
            }
            Rectangle r = new Rectangle().x(10).y(90).width(200).height(20);
            Rectangle r2 = new Rectangle().x(10).y(90).width(300).height(400);
            // GuiSlider(r, "", "", floatBox(100), 1, 64);
            // GuiButton(r2, "SWAIN");
            picker.draw();

            DrawFPS(20, 20);
            EndDrawing();
        }

        UnloadRenderTexture(canvas);
        CloseWindow();
    }

}
