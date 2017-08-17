package glass.phil.monzo.model.transactions;

import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import glass.phil.monzo.TestData;
import glass.phil.monzo.model.transactions.Transaction.Merchant;

import static glass.phil.monzo.model.transactions.TestTransactions.AMAZON;
import static glass.phil.monzo.model.transactions.TestTransactions.ASDA;
import static glass.phil.monzo.model.transactions.TestTransactions.STARBUCKS;
import static glass.phil.monzo.model.transactions.TestTransactions.atMerchant;

@SuppressWarnings("WeakerAccess")
public final class TransactionGenerator {
  private static final Merchant[] MERCHANTS = {AMAZON, ASDA, STARBUCKS};

  /** Generate a list of transactions sorted by creation time, with the oldest transaction at index 0. */
  public static List<TestTransaction> generateTransactions(int count) {
    final List<TestTransaction> transactions = new ArrayList<>(count);
    for (int index = 0; index < count; index++) {
      transactions.add(atMerchant(MERCHANTS[index % MERCHANTS.length])
          .withCreated(TestData.NOW.minus(count - index, ChronoUnit.HOURS)));
    }
    return transactions;
  }

  private TransactionGenerator() {
    throw new AssertionError("No instances");
  }
}
