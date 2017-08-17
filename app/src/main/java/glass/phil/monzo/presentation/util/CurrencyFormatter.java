package glass.phil.monzo.presentation.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;

import org.joda.money.Money;

import java.util.Locale;

import glass.phil.monzo.R;

import static glass.phil.monzo.presentation.util.Spans.append;
import static glass.phil.monzo.presentation.util.Spans.applyFullWidthSpan;

public final class CurrencyFormatter {
  public static CharSequence formatBalance(Context context, Money amount) {
    final SpannableStringBuilder builder = new SpannableStringBuilder();
    append(builder, amount.getCurrencyUnit().getSymbol(locale(context)), new RelativeSizeSpan(0.6f));
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
    append(builder, localAmount.getCurrencyUnit().getSymbol(locale(context)));
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

    append(builder, amount.getCurrencyUnit().getSymbol(locale(context)), new RelativeSizeSpan(0.6f));
    append(builder, amount.getAmountMajor().toPlainString());
    append(builder, minorPart(amount), new RelativeSizeSpan(0.7f));
    return builder;
  }

  public static String unformatted(Context context, Money amount) {
    amount = amount.abs();
    return amount.getCurrencyUnit().getSymbol(locale(context)) +
        amount.getAmountMajor().toPlainString() +
        minorPart(amount);
  }

  @SuppressWarnings("deprecation")
  private static Locale locale(Context context) {
    final Configuration configuration = context.getResources().getConfiguration();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return configuration.getLocales().get(0);
    } else {
      return configuration.locale;
    }
  }

  private static String minorPart(Money amount) {
    final int decimalPlaces = amount.getCurrencyUnit().getDecimalPlaces();
    return decimalPlaces > 0 ? String.format(".%0" + decimalPlaces + "d", amount.getMinorPart()) : "";
  }

  private CurrencyFormatter() {
    throw new AssertionError("No instances");
  }
}
