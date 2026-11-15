package se.chalmers.group7.core.operations.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ByteCompression {
  // Source - https://stackoverflow.com/q/51332314
  // Posted by Lev Knoblock, modified by community. See post 'Timeline' for change history
  // Retrieved 2026-11-15, License - CC BY-SA 4.0

  public static byte[] compress(byte[] in) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      //            DeflaterOutputStream defl = new DeflaterOutputStream(out);
      GZIPOutputStream gzip = new GZIPOutputStream(out);
      gzip.write(in);
      gzip.flush();
      gzip.close();

      return out.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(150);
      return null;
    }
  }

  public static GZIPInputStream decompress(byte[] in) {
    try {
      //            ByteArrayOutputStream out = new ByteArrayOutputStream();
      //            InflaterOutputStream infl = new InflaterOutputStream(out);

      GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(in));

      //            gzip.write(in);
      //            gzip.flush();
      //            gzip.close();

      return gzip;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(150);
      return null;
    }
  }
}
