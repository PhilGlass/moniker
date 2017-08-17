package glass.phil.monzo.model.balance;

import com.google.auto.value.AutoValue;

import glass.phil.auto.moshi.AutoMoshi;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface BalanceApi {
  @GET("balance") Single<BalanceResponse> balance(@Query("account_id") String accountId);

  @AutoValue @AutoMoshi abstract class BalanceResponse {
    abstract String currency();
    abstract long balance();
    abstract long spend_today();
  }
}
