package glass.phil.monzo.presentation.util;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

public final class TextViews {
  public static void showText(TextView textView, @Nullable CharSequence text) {
    textView.setVisibility(View.VISIBLE);
    textView.setText(text);
  }

  public static void showText(TextView textView, @StringRes int text) {
    textView.setVisibility(View.VISIBLE);
    textView.setText(text);
  }

  private TextViews() {
    throw new AssertionError("No instances");
  }
}
