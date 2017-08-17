package glass.phil.monzo.presentation.util;

import android.content.Intent;
import android.support.annotation.Nullable;

public final class Intents {
  public static boolean hasAction(@Nullable Intent intent, String action) {
    return intent != null && action.equals(intent.getAction());
  }

  private Intents() {
    throw new AssertionError("No instances");
  }
}
