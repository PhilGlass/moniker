package glass.phil.monzo.presentation.util;

import android.app.Activity;

import glass.phil.monzo.R;

public final class Windows {
  public static void setDefaultBackground(Activity activity) {
    activity.getWindow().setBackgroundDrawableResource(android.R.color.white);
  }

  public static void setColorPrimaryBackground(Activity activity) {
    activity.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
  }

  private Windows() {
    throw new AssertionError("No instances");
  }
}
