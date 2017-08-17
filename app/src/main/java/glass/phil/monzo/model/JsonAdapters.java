package glass.phil.monzo.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.ToJson;

import org.joda.money.Money;
import org.threeten.bp.Instant;

import java.util.Date;

import glass.phil.auto.moshi.AutoMoshi;

final class JsonAdapters {
  static final class InstantAdapter {
    @FromJson Instant fromDate(Date date) {
      return Instant.ofEpochMilli(date.getTime());
    }

    @ToJson Date toDate(Instant instant) {
      return new Date(instant.toEpochMilli());
    }
  }

  static final class MoneyAdapter {
    @FromJson Money fromString(String string) {
      return Money.parse(string);
    }

    @ToJson String toString(Money money) {
      return money.toString();
    }
  }

  @AutoMoshi.Factory static abstract class AutoMoshiFactory implements JsonAdapter.Factory {
    static JsonAdapter.Factory create() {
      return new AutoMoshi_JsonAdapters_AutoMoshiFactory();
    }
  }

  private JsonAdapters() {
    throw new AssertionError("No instances");
  }
}
