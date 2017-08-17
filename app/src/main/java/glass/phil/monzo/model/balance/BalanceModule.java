package glass.phil.monzo.model.balance;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import glass.phil.monzo.model.Authorized;
import glass.phil.monzo.model.Refreshable;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.balance.BalanceManager.Balance;
import retrofit2.Retrofit;

@Module public abstract class BalanceModule {
  @Provides static BalanceApi balanceApi(@Authorized Retrofit retrofit) {
    return retrofit.create(BalanceApi.class);
  }

  @Provides @Singleton static Store<Balance> balanceStore(Store.Factory factory) {
    return factory.create(Balance.class, "store::balance");
  }

  @Binds @Singleton abstract BalanceManager balanceManager(BalanceManagerImpl balanceManager);
  @Binds @IntoSet abstract Refreshable refreshable(BalanceManager refreshable);
}
