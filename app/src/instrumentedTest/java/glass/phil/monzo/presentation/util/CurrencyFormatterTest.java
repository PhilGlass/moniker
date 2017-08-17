package glass.phil.monzo.presentation.util;

import android.support.test.runner.AndroidJUnit4;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public final class CurrencyFormatterTest {
  private static final Money NEGATIVE = Money.ofMinor(CurrencyUnit.GBP, -1234);
  private static final Money ZERO = Money.ofMinor(CurrencyUnit.GBP, 0);
  private static final Money POSITIVE = Money.ofMinor(CurrencyUnit.GBP, 1234);

  @Test public void balance() {
    // Balance and spending are always non-negative
    assertThat(formatBalance(ZERO)).isEqualTo("£0.00");
    assertThat(formatBalance(POSITIVE)).isEqualTo("£12.34");
  }

  @Test public void transaction() {
    assertThat(formatTransaction(NEGATIVE)).isEqualTo("12.34");
    assertThat(formatTransaction(ZERO)).isEqualTo("0.00");
    assertThat(formatTransaction(POSITIVE)).isEqualTo("+12.34");
  }

  @Test public void local() {
    assertThat(formatLocal(Money.ofMinor(CurrencyUnit.USD, -1234))).isEqualTo("US$12.34");
    assertThat(formatLocal(Money.ofMinor(CurrencyUnit.USD, 0))).isEqualTo("US$0.00");
    assertThat(formatLocal(Money.ofMinor(CurrencyUnit.USD, 1234))).isEqualTo("US$12.34");
  }

  @Test public void detail() {
    assertThat(formatDetail(NEGATIVE)).isEqualTo("£12.34");
    assertThat(formatDetail(ZERO)).isEqualTo("£0.00");
    assertThat(formatDetail(POSITIVE)).isEqualTo("£12.34");
  }

  @Test public void unformatted() {
    assertThat(CurrencyFormatter.unformatted(ConfigurationContexts.uk(), NEGATIVE)).isEqualTo("£12.34");
    assertThat(CurrencyFormatter.unformatted(ConfigurationContexts.uk(), ZERO)).isEqualTo("£0.00");
    assertThat(CurrencyFormatter.unformatted(ConfigurationContexts.uk(), POSITIVE)).isEqualTo("£12.34");
  }

  private static String formatBalance(Money amount) {
    return CurrencyFormatter.formatBalance(ConfigurationContexts.uk(), amount).toString();
  }

  private static String formatTransaction(Money amount) {
    return CurrencyFormatter.formatTransaction(ConfigurationContexts.uk(), amount).toString();
  }

  private static String formatLocal(Money localAmount) {
    return CurrencyFormatter.formatLocal(ConfigurationContexts.uk(), localAmount).toString();
  }

  private static String formatDetail(Money amount) {
    return CurrencyFormatter.formatDetail(ConfigurationContexts.uk(), amount, false).toString();
  }
}
