package glass.phil.monzo.model.account;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;

import java.util.List;

import glass.phil.auto.moshi.AutoMoshi;
import io.reactivex.Single;
import retrofit2.http.GET;

interface AccountApi {
  @GET("accounts") Single<AccountsResponse> accounts();

  @AutoValue @AutoMoshi abstract class AccountsResponse {
    abstract List<Account> accounts();

    @AutoValue @AutoMoshi static abstract class Account {
      enum AccountType {
        @Json(name = "uk_prepaid") PREPAID,
        @Json(name = "uk_retail") RETAIL
      }

      abstract String id();
      abstract AccountType type();
    }
  }
}
