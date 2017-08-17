package glass.phil.monzo.core;

import android.support.annotation.Nullable;

public final class Strings {
  @Nullable public static String emptyToNull(@Nullable String in) {
    return in == null || in.isEmpty() ? null : in;
  }

  public static String nullToEmpty(@Nullable String in) {
    return in == null ? "" : in;
  }

  private Strings() {
    throw new AssertionError("No instances");
  }
}
