package se.chalmers.group7.core.operations.utils;

import static com.raylib.Raylib.*;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
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
    return ByteCompression.compress(diff);
  }

  // 3. Apply the diff (call this exact same method for BOTH undo and redo!)
  public static void applyDiff(Texture texture, byte[] diff, int chunkSize) {
    byte[] pixels = readPixels(texture);

    try (GZIPInputStream in = ByteCompression.decompress(diff)) {
      byte[] buffer = new byte[chunkSize];

      int offset = 0;
      int bytesRead;

      while ((bytesRead = in.read(buffer)) != -1) {
        if (offset + bytesRead > pixels.length) {
          throw new IOException("Diff is larger than texture");
        }

        for (int i = 0; i < bytesRead; i++) {
          pixels[offset + i] ^= buffer[i];
        }

        offset += bytesRead;
      }

      if (offset != pixels.length) {
        throw new IOException("Diff size does not match texture");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try (BytePointer ptr = new BytePointer(pixels)) {
      UpdateTexture(texture, ptr); // glTexSubImage2D copies synchronously before returning
    }
  }

  public static void applyDiff(Texture texture, byte[] diff) {
    applyDiff(texture, diff, 8192);
  }
}
