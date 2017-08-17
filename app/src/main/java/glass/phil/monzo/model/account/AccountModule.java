package glass.phil.monzo.model.account;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.model.Authorized;
import glass.phil.monzo.model.Store;
import retrofit2.Retrofit;

@Module public abstract class AccountModule {
  @Provides static AccountApi accountApi(@Authorized Retrofit retrofit) {
    return retrofit.create(AccountApi.class);
  }

  @Provides @Singleton static Store<String> accountIdStore(Store.Factory factory) {
    return factory.create(String.class, "store::account_id");
  }

  @Binds @Singleton abstract AccountManager accountManager(AccountManagerImpl accountManager);
}
