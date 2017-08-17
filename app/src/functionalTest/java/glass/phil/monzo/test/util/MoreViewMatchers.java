package glass.phil.monzo.test.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Locale;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public final class MoreViewMatchers {
  public static ViewAssertion doesNotExistOrNotDisplayed() {
    return (view, noViewFoundException) -> {
      if (view != null) {
        ViewMatchers.assertThat("does not exist or is not displayed", view, not(isDisplayed()));
      }
    };
  }

  public static Matcher<View> displayedWithText(String text) {
    return allOf(isDisplayed(), withText(text));
  }

  public static Matcher<View> displayedWithText(@StringRes int text) {
    return allOf(isDisplayed(), withText(text));
  }

  public static Matcher<View> withTextColor(TextColor textColor) {
    return new BoundedMatcher<View, TextView>(TextView.class) {
      @Override protected boolean matchesSafely(TextView item) {
        final CharSequence text = item.getText();
        if (text instanceof Spanned) {
          final Spanned spanned = (Spanned) text;
          final ForegroundColorSpan[] colorSpans = spanned.getSpans(0, spanned.length(), ForegroundColorSpan.class);
          if (colorSpans.length > 0) {
            // A ForegroundColorSpan overrides the TextView's color property. When multiple ForegroundColorSpans
            // are applied, only the last has any effect (see android.text.TextLine).
            return colorMatches(colorSpans[colorSpans.length - 1].getForegroundColor());
          }
        }
        return colorMatches(item.getCurrentTextColor());
      }

      private boolean colorMatches(@ColorInt int color) {
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return textColor.matches(hsv);
      }

      @Override public void describeTo(Description description) {
        description.appendText("with ")
            .appendValue(textColor.name().toLowerCase(Locale.US))
            .appendText(" text");
      }
    };
  }

  public enum TextColor {
    RED {
      @Override boolean matches(@Size(3) float[] hsv) {
        return hsv[0] <= 10 || hsv[0] >= 355;
      }
    },
    GREEN {
      @Override boolean matches(@Size(3) float[] hsv) {
        return hsv[0] >= 81 && hsv[0] <= 140;
      }
    },
    BLACK {
      @Override boolean matches(@Size(3) float[] hsv) {
        return hsv[2] == 0;
      }
    };

    abstract boolean matches(@Size(3) float[] hsv);
  }

  private MoreViewMatchers() {
    throw new AssertionError("No instances");
  }
}
