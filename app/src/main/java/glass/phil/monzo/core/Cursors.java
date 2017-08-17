package glass.phil.monzo.core;

import android.database.Cursor;
import android.support.annotation.Nullable;

public final class Cursors {
  public static boolean getBoolean(Cursor cursor, String column) {
    return cursor.getInt(cursor.getColumnIndexOrThrow(column)) == 1;
  }

  public static double getDouble(Cursor cursor, String column) {
    return cursor.getDouble(cursor.getColumnIndexOrThrow(column));
  }

  public static long getLong(Cursor cursor, String column) {
    return cursor.getLong(cursor.getColumnIndexOrThrow(column));
  }

  @Nullable public static String getString(Cursor cursor, String column) {
    return cursor.getString(cursor.getColumnIndexOrThrow(column));
  }

  private Cursors() {
    throw new AssertionError("No instances");
  }
}
