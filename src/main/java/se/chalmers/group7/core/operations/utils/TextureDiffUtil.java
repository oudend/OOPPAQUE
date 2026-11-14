package se.chalmers.group7.core.operations.utils;

import static com.raylib.Raylib.*;

import org.bytedeco.javacpp.BytePointer;

public class TextureDiffUtil {
  public static byte[] readPixels(Texture texture) {
    Image img = LoadImageFromTexture(texture);
    if (img.data() == null || img.data().isNull()) {
      throw new IllegalStateException("LoadImageFromTexture failed for texture id=" + texture.id());
    }
    byte[] pixels = new byte[texture.width() * texture.height() * 4];
    new BytePointer(img.data()).get(pixels);
    UnloadImage(img);
    return pixels;
  }

  // 2. Compute the diff between before and after
  public static byte[] computeDiff(byte[] before, byte[] after) {
    byte[] diff = new byte[before.length];
    for (int i = 0; i < diff.length; i++) {
      diff[i] = (byte) (before[i] ^ after[i]);
    }
    return diff;
  }

  // 3. Apply the diff (call this exact same method for BOTH undo and redo!)
  public static void applyDiff(Texture texture, byte[] diff) {
    byte[] pixels = readPixels(texture);
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] ^= diff[i];
    }
    try (BytePointer ptr = new BytePointer(pixels)) {
      UpdateTexture(texture, ptr); // glTexSubImage2D copies synchronously before returning
    }
  }
}
