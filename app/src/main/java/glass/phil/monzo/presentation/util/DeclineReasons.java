package glass.phil.monzo.presentation.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;

import glass.phil.monzo.R;
import glass.phil.monzo.model.transactions.Transaction.DeclineReason;

public final class DeclineReasons {
  public static CharSequence declined(Context context) {
    final SpannableStringBuilder declined = new SpannableStringBuilder(context.getString(R.string.declined));
    Spans.applyFullWidthSpan(declined, new TextAppearanceSpan(context, R.style.TextAppearance_Transaction_Declined));
    return declined;
  }

  @StringRes public static int declineReason(DeclineReason reason) {
    switch (reason) {
      case CARD_BLOCKED:
        return R.string.declined_card_blocked;
      case CARD_INACTIVE:
        return R.string.declined_card_inactive;
      case INSUFFICIENT_FUNDS:
        return R.string.declined_insufficient_funds;
      case OTHER:
        return R.string.declined_other;
    }
    throw new AssertionError();
  }

  private DeclineReasons() {
    throw new AssertionError("No instances");
  }
}
