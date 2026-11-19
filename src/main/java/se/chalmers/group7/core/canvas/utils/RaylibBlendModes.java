package se.chalmers.group7.core.canvas.utils;

import static com.raylib.Raylib.*;

import se.chalmers.group7.core.canvas.BlendingMode;

public class RaylibBlendModes {
  public static void applyBlendMode(BlendingMode blendingMode) {
    switch (blendingMode) {
      case NORMAL:
        BeginBlendMode(BLEND_ALPHA);
        break;
      case REPLACE:
        // 1 * src + 0 * dst = exact copy of src (RGB and Alpha)
        rlSetBlendFactors(RL_ONE, RL_ZERO, RL_FUNC_ADD);
        BeginBlendMode(BLEND_CUSTOM);
        break;
      case LINEAR_SUBTRACTIVE_ERASE:
        rlSetBlendFactorsSeparate(
            RL_ZERO, RL_ONE, // RGB: keep destination RGB unchanged
            RL_ONE, RL_ONE, // Alpha: 1 * dstAlpha - 1 * srcAlpha
            RL_FUNC_ADD, RL_FUNC_REVERSE_SUBTRACT);
        BeginBlendMode(BLEND_CUSTOM_SEPARATE);
        break;
    }
  }
}
