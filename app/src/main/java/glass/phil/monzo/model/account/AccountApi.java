package glass.phil.monzo.model.account;

import com.google.auto.value.AutoValue;

import java.util.List;

import glass.phil.auto.moshi.AutoMoshi;
import io.reactivex.Single;
import retrofit2.http.GET;

interface AccountApi {
  @GET("accounts") Single<AccountsResponse> accounts();

  @AutoValue @AutoMoshi abstract class AccountsResponse {
    abstract List<Account> accounts();

    @AutoValue @AutoMoshi static abstract class Account {
      abstract String id();
    }
  }
}
