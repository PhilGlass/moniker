package glass.phil.monzo.presentation.util;

import android.graphics.Outline;
import android.support.annotation.Dimension;
import android.view.View;
import android.view.ViewOutlineProvider;

public final class Outlines {
  public static ViewOutlineProvider roundRect(@Dimension(unit = Dimension.PX) float cornerRadius) {
    return new ViewOutlineProvider() {
      @Override public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
      }
    };
  }

  private Outlines() {
    throw new AssertionError("No instances");
  }
}
