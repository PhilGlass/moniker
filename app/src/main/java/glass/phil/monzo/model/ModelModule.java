package glass.phil.monzo.model;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.model.account.AccountModule;
import glass.phil.monzo.model.auth.AuthInterceptor;
import glass.phil.monzo.model.auth.AuthModule;
import glass.phil.monzo.model.balance.BalanceModule;
import glass.phil.monzo.model.transactions.TransactionModule;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module(includes = {
    AuthModule.class,
    AccountModule.class,
    BalanceModule.class,
    TransactionModule.class
})
public abstract class ModelModule {
  @Provides @Singleton static Moshi moshi() {
    return new Moshi.Builder()
        .add(Date.class, new Rfc3339DateJsonAdapter())
        .add(new JsonAdapters.InstantAdapter())
        .add(new JsonAdapters.MoneyAdapter())
        .add(JsonAdapters.AutoMoshiFactory.create())
        .build();
  }

  @Provides @Singleton static OkHttpClient baseOkHttp() {
    return new OkHttpClient();
  }

  @Provides @Singleton static Retrofit baseRetrofit(HttpUrl baseUrl, OkHttpClient okHttp, Moshi moshi) {
    return new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttp)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
  }

  @Provides @Singleton @Authorized static OkHttpClient authorizedOkHttp(OkHttpClient base,
      AuthInterceptor interceptor) {
    return base.newBuilder().addInterceptor(interceptor).build();
  }

  @Provides @Singleton @Authorized static Retrofit authorizedRetrofit(Retrofit base, @Authorized OkHttpClient client) {
    return base.newBuilder().client(client).build();
  }
}
