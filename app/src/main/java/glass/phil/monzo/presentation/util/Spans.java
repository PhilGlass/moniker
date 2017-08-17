package glass.phil.monzo.presentation.util;

import android.text.SpannableStringBuilder;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

final class Spans {
  static void append(SpannableStringBuilder builder, CharSequence text, Object... spans) {
    final int start = builder.length();
    builder.append(text);
    final int end = builder.length();
    for (Object span : spans) {
      builder.setSpan(span, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  static void applyFullWidthSpan(SpannableStringBuilder builder, Object span) {
    builder.setSpan(span, 0, builder.length(), SPAN_INCLUSIVE_INCLUSIVE);
  }

  private Spans() {
    throw new AssertionError("No instances");
  }
}
