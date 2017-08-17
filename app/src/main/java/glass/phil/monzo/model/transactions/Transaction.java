package glass.phil.monzo.model.transactions;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.ryanharter.auto.value.parcel.ParcelAdapter;

import org.joda.money.Money;
import org.threeten.bp.Instant;

import glass.phil.monzo.core.Preconditions;
import glass.phil.monzo.model.ParcelAdapters.InstantAdapter;
import glass.phil.monzo.model.ParcelAdapters.MoneyAdapter;

@AutoValue public abstract class Transaction implements Parcelable {
  @SuppressWarnings("unused")
  public enum Category {
    BILLS,
    CASH,
    EATING_OUT,
    ENTERTAINMENT,
    EXPENSES,
    GENERAL,
    GROCERIES,
    HOLIDAYS,
    MONDO,
    SHOPPING,
    TRANSPORT
  }

  public enum DeclineReason {
    CARD_BLOCKED,
    CARD_INACTIVE,
    INSUFFICIENT_FUNDS,
    OTHER
  }

  static Builder builder() {
    return new AutoValue_Transaction.Builder();
  }

  public abstract String id();
  public abstract Category category();
  @ParcelAdapter(InstantAdapter.class)
  public abstract Instant created();
  @ParcelAdapter(MoneyAdapter.class)
  public abstract Money amount();
  @ParcelAdapter(MoneyAdapter.class)
  public abstract Money localAmount();
  @Nullable public abstract DeclineReason declineReason();
  public abstract boolean topUp();
  public abstract boolean hideAmount();
  @Nullable public abstract String notes();
  @Nullable public abstract Merchant merchant();

  public boolean inForeignCurrency() {
    return !amount().getCurrencyUnit().equals(localAmount().getCurrencyUnit());
  }

  public boolean declined() {
    return declineReason() != null;
  }

  /** A transaction that is not a top up will always have a merchant. */
  public final Merchant requireMerchant() {
    return Preconditions.checkNotNull(merchant());
  }

  @AutoValue.Builder static abstract class Builder {
    abstract Builder id(String id);
    abstract Builder category(Category category);
    abstract Builder created(Instant created);
    abstract Builder amount(Money amount);
    abstract Builder localAmount(Money localAmount);
    abstract Builder declineReason(@Nullable DeclineReason declineReason);
    abstract Builder topUp(boolean topUp);
    abstract Builder hideAmount(boolean hideAmount);
    abstract Builder notes(@Nullable String notes);
    abstract Builder merchant(@Nullable Merchant merchant);
    abstract Transaction build();
  }

  @AutoValue public static abstract class Merchant implements Parcelable {
    static Merchant create(String id, String name, boolean online, @Nullable String logoUrl, Address address) {
      return new AutoValue_Transaction_Merchant(id, name, online, logoUrl, address);
    }

    public abstract String id();
    public abstract String name();
    public abstract boolean online();
    @Nullable public abstract String logoUrl();
    public abstract Address address();

    @AutoValue public static abstract class Address implements Parcelable {
      static Address create(String formattedAddress, double latitude, double longitude) {
        return new AutoValue_Transaction_Merchant_Address(formattedAddress, latitude, longitude);
      }

      public abstract String formattedAddress();
      public abstract double latitude();
      public abstract double longitude();
    }
  }
}
