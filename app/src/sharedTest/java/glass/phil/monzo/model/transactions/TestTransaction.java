package glass.phil.monzo.model.transactions;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.threeten.bp.Instant;

import glass.phil.monzo.model.transactions.Transaction.Category;
import glass.phil.monzo.model.transactions.Transaction.DeclineReason;
import glass.phil.monzo.model.transactions.Transaction.Merchant;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData;

@SuppressWarnings({"unused", "WeakerAccess"})
@AutoValue public abstract class TestTransaction {
  public static Builder builder() {
    return new AutoValue_TestTransaction.Builder();
  }

  public abstract String id();
  public abstract Category category();
  public abstract Instant created();
  public abstract Money amount();
  public abstract Money localAmount();
  @Nullable public abstract DeclineReason declineReason();
  public abstract boolean topUp();
  public abstract boolean hideAmount();
  public abstract boolean includeInSpending();
  @Nullable public abstract String notes();
  @Nullable public abstract Merchant merchant();

  public abstract Builder toBuilder();

  public abstract TestTransaction withCreated(Instant created);
  public abstract TestTransaction withLocalAmount(@Nullable Money localAmount);
  public abstract TestTransaction withDeclineReason(@Nullable DeclineReason declineReason);
  public abstract TestTransaction withHideAmount(boolean hideAmount);
  public abstract TestTransaction withIncludeInSpending(boolean includeInSpending);
  public abstract TestTransaction withNotes(@Nullable String notes);
  public abstract TestTransaction withMerchant(@Nullable Merchant merchant);

  public final TestTransaction withAmount(long amountPennies) {
    return toBuilder().amount(Money.ofMinor(CurrencyUnit.GBP, amountPennies)).build();
  }

  public final Transaction toTransaction() {
    return TestTransactionMapper.toTransaction(this);
  }

  public final TransactionData toTransactionData() {
    return TestTransactionMapper.toTransactionData(this);
  }

  @AutoValue.Builder public static abstract class Builder {
    public abstract Builder id(String id);
    public abstract Builder category(Category category);
    public abstract Builder created(Instant created);
    public abstract Builder amount(Money amount);
    public abstract Builder localAmount(Money localAmount);
    public abstract Builder declineReason(@Nullable DeclineReason declineReason);
    public abstract Builder topUp(boolean topUp);
    public abstract Builder hideAmount(boolean hideAmount);
    public abstract Builder includeInSpending(boolean includeInSpending);
    public abstract Builder notes(@Nullable String notes);
    public abstract Builder merchant(@Nullable Merchant merchant);
    public abstract TestTransaction build();
  }
}
