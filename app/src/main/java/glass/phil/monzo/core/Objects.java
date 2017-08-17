package glass.phil.monzo.core;

import android.support.annotation.Nullable;

public final class Objects {
  public static boolean equal(@Nullable Object first, @Nullable Object second) {
    return first == null ? second == null : first.equals(second);
  }

  private Objects() {
    throw new AssertionError("No instances.");
  }
}
