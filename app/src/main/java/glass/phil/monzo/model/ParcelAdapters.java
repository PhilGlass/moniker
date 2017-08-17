package glass.phil.monzo.model;

import android.os.Parcel;

import com.ryanharter.auto.value.parcel.TypeAdapter;

import org.joda.money.Money;
import org.threeten.bp.Instant;

public final class ParcelAdapters {
  public static final class InstantAdapter implements TypeAdapter<Instant> {
    @Override public Instant fromParcel(Parcel in) {
      return Instant.parse(in.readString());
    }

    @Override public void toParcel(Instant value, Parcel dest) {
      dest.writeString(value.toString());
    }
  }

  public static final class MoneyAdapter implements TypeAdapter<Money> {
    @Override public Money fromParcel(Parcel in) {
      return Money.parse(in.readString());
    }

    @Override public void toParcel(Money value, Parcel dest) {
      dest.writeString(value.toString());
    }
  }

  private ParcelAdapters() {
    throw new AssertionError("No instances");
  }
}
