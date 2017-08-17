package glass.phil.monzo.model.transactions;

import com.google.auto.value.AutoValue;

import org.joda.money.Money;

@AutoValue public abstract class TransactionHistory {
  public static TransactionHistory create(long transactionCount, Money averageSpend, Money totalSpend) {
    return new AutoValue_TransactionHistory(transactionCount, averageSpend, totalSpend);
  }

  public abstract long transactionCount();
  public abstract Money averageSpend();
  public abstract Money totalSpend();
}
