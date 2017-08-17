package glass.phil.monzo.core;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public final class Bundles {
  public static Bundle just(String key, Parcelable value) {
    final Bundle bundle = new Bundle(1);
    bundle.putParcelable(key, value);
    return bundle;
  }

  public static @Nullable <T extends Parcelable> T getParcelable(@Nullable Bundle bundle, String key) {
    return bundle == null ? null : bundle.getParcelable(key);
  }

  private Bundles() {
    throw new AssertionError("No instances");
  }
}
