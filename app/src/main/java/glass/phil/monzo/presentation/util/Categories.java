package glass.phil.monzo.presentation.util;

import android.support.annotation.DrawableRes;

import glass.phil.monzo.R;
import glass.phil.monzo.model.transactions.Transaction.Category;

public final class Categories {
  @DrawableRes public static int iconFor(Category category) {
    switch (category) {
      case BILLS:
        return R.drawable.ic_bills;
      case CASH:
        return R.drawable.ic_cash;
      case EATING_OUT:
        return R.drawable.ic_eating_out;
      case ENTERTAINMENT:
        return R.drawable.ic_entertainment;
      case EXPENSES:
        return R.drawable.ic_expenses;
      case GROCERIES:
        return R.drawable.ic_groceries;
      case HOLIDAYS:
        return R.drawable.ic_holidays;
      case MONDO:
        return R.drawable.ic_top_up;
      case SHOPPING:
        return R.drawable.ic_shopping;
      case TRANSPORT:
        return R.drawable.ic_transport;
      default:
        return R.drawable.ic_general;
    }
  }

  private Categories() {
    throw new AssertionError("No instances");
  }
}
