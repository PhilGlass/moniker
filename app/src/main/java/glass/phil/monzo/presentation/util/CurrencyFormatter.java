package glass.phil.monzo.presentation.util;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;

import org.joda.money.Money;

import glass.phil.monzo.R;

import static glass.phil.monzo.presentation.util.Spans.append;
import static glass.phil.monzo.presentation.util.Spans.applyFullWidthSpan;

public final class CurrencyFormatter {
  public static CharSequence formatBalance(Context context, Money amount) {
    final SpannableStringBuilder builder = new SpannableStringBuilder();
    append(builder, amount.getCurrencyUnit().getSymbol(), new RelativeSizeSpan(0.6f));
    append(builder, amount.getAmountMajor().toPlainString());
    append(builder, minorPart(amount), new RelativeSizeSpan(0.7f),
        new ForegroundColorSpan(context.getColor(R.color.translucent_white)));
    return builder;
  }

  public static CharSequence formatTransaction(Context context, Money amount) {
    final SpannableStringBuilder builder = new SpannableStringBuilder();
    applyFullWidthSpan(builder, new TextAppearanceSpan(context, R.style.TextAppearance_Transaction_Amount));

    if (amount.isPositive()) {
      applyFullWidthSpan(builder, new ForegroundColorSpan(context.getColor(R.color.positive_green)));
      append(builder, "+");
    }
    amount = amount.abs();

    append(builder, amount.getAmountMajor().toPlainString());
    append(builder, minorPart(amount), new RelativeSizeSpan(0.7f));
    return builder;
  }

  public static CharSequence formatLocal(Context context, Money localAmount) {
    final SpannableStringBuilder builder = new SpannableStringBuilder();
    applyFullWidthSpan(builder, new TextAppearanceSpan(context, R.style.TextAppearance_Transaction_LocalAmount));

    localAmount = localAmount.abs();
    append(builder, localAmount.getCurrencyUnit().getSymbol());
    append(builder, localAmount.getAmountMajor().toPlainString());
    append(builder, minorPart(localAmount));
    return builder;
  }

  public static CharSequence formatDetail(Context context, Money amount, boolean declined) {
    final SpannableStringBuilder builder = new SpannableStringBuilder();
    applyFullWidthSpan(builder, new TextAppearanceSpan(context, R.style.TextAppearance_Detail_Amount));

    if (declined) {
      applyFullWidthSpan(builder, new ForegroundColorSpan(context.getColor(R.color.declined_red)));
    } else if (amount.isPositive()) {
      applyFullWidthSpan(builder, new ForegroundColorSpan(context.getColor(R.color.positive_green)));
    }
    amount = amount.abs();

    append(builder, amount.getCurrencyUnit().getSymbol(), new RelativeSizeSpan(0.6f));
    append(builder, amount.getAmountMajor().toPlainString());
    append(builder, minorPart(amount), new RelativeSizeSpan(0.7f));
    return builder;
  }

  public static String unformatted(Money amount) {
    amount = amount.abs();
    return amount.getCurrencyUnit().getSymbol() +
        amount.getAmountMajor().toPlainString() +
        minorPart(amount);
  }

  private static String minorPart(Money amount) {
    final int decimalPlaces = amount.getCurrencyUnit().getDecimalPlaces();
    return decimalPlaces > 0 ? String.format(".%0" + decimalPlaces + "d", amount.getMinorPart()) : "";
  }

  private CurrencyFormatter() {
    throw new AssertionError("No instances");
  }
}
