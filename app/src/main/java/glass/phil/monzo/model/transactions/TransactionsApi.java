package glass.phil.monzo.model.transactions;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.threeten.bp.Instant;

import java.util.List;
import java.util.Map;

import glass.phil.auto.moshi.AutoMoshi;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface TransactionsApi {
  @GET("transactions?expand[]=merchant") Single<TransactionsResponse> transactions(
      @Query("account_id") String accountId,
      @Query("limit") int limit,
      @Query("since") @Nullable String since
  );

  @AutoValue @AutoMoshi abstract class TransactionsResponse {
    abstract List<TransactionData> transactions();

    @AutoValue @AutoMoshi static abstract class TransactionData {
      abstract String id();
      abstract String category();
      abstract Instant created();
      abstract String currency();
      abstract long amount();
      abstract String local_currency();
      abstract long local_amount();
      @Nullable abstract String decline_reason();
      abstract boolean is_load();
      abstract boolean include_in_spending();
      abstract String notes();
      abstract Map<String, String> metadata();
      @Nullable abstract MerchantData merchant();

      @AutoValue @AutoMoshi static abstract class MerchantData {
        abstract String id();
        abstract String name();
        abstract boolean online();
        abstract String logo();
        abstract Address address();

        @AutoValue @AutoMoshi static abstract class Address {
          abstract double latitude();
          abstract double longitude();
          abstract String short_formatted();
        }
      }
    }
  }
}
