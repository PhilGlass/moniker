package glass.phil.monzo;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneOffset;

import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.model.balance.BalanceManager;
import glass.phil.monzo.model.transactions.TransactionHistory;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public final class TestData {
  public static Instant NOW = LocalDateTime.of(2015, Month.JULY, 15, 12, 0).toInstant(ZoneOffset.UTC);

  public static final String STATE = "cb64daea-9322-474c-b6dc-a4fb9aa00176";
  public static final String CODE = "lfQoiNjpVpQPBgP1UpPzPuY5jJP6PrwECJQ9.lfQqhZP6Pt9okEYvF2ewGD50EgHdTKH5ZNoXlOMdF2G" +
      "5CgGZYEF3DuHpSJQslOHpVqL0VArdTgBfUqTzPtsokJP6IYQ5VAHgTAF2TfdphuYwPqvpFEC0hNUcGNCmTKHdTKsUTukOG1sIFCwtBBavYsT3G" +
      "JPzPuCwPqvpkEUsjs8dTKHdVAGejsoBTAIrG1oTC2ooGujePpdpkpP6PqPpmX.DKT9VK4YHb1dBe0So_-nQPzPi_MaIA9xMnnTPsuatJ8";
  public static final String ACCESS_TOKEN = "vbQsyTxrLrQRFaR1MrRhRmI5xXR6RpkCEXQ9.vbQqzHR6Rn9swCIlB2ckAD50CaZdNWZ5HTs" +
      "JvSUdB2A5EaAHICB3DmZrOXQovSZrLqV0LGpcNaJdMWVhRnoswXR6NGJ5LGVcNqJ0NHdppmIkRqlrwT9iCaZdNWZ5GGIaFGsYAVA0DVR2GEYQE" +
      "oBrOXQ1zHR6RmEaACQuNWZdNWp2xCQBEWVdATwBGUwlBDA3NHRhRmBrLrRbRm0.2Oqb0wN-cRGv0WHoR233xew3QbRkpwgv8Ze0lIFRg1J";
  public static final String ACCOUNT_ID = "acc_00008B6M1pROHJ8sEu9l0I";

  public static BalanceManager.Balance balance(long balancePennies, long spentTodayPennies) {
    final Money balance = Money.ofMinor(CurrencyUnit.GBP, balancePennies);
    final Money spentToday = Money.ofMinor(CurrencyUnit.GBP, spentTodayPennies);
    return BalanceManager.Balance.create(balance, spentToday);
  }

  public static Optional<TransactionHistory> history(long transactions, long averagePennies, long totalPennies) {
    final Money averageSpend = Money.ofMinor(CurrencyUnit.GBP, averagePennies);
    final Money totalSpend = Money.ofMinor(CurrencyUnit.GBP, totalPennies);
    return Optional.of(TransactionHistory.create(transactions, averageSpend, totalSpend));
  }

  private TestData() {
    throw new AssertionError("No instances");
  }
}
