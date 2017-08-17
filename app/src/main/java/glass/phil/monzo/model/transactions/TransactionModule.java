package glass.phil.monzo.model.transactions;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import glass.phil.monzo.model.Authorized;
import glass.phil.monzo.model.Refreshable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

@Module public abstract class TransactionModule {
  @Provides static TransactionsApi transactionsApi(@Authorized Retrofit retrofit) {
    return retrofit.create(TransactionsApi.class);
  }

  @Provides @Singleton static BriteDatabase briteDatabase(TransactionsDb db) {
    return new SqlBrite.Builder().build().wrapDatabaseHelper(db, Schedulers.io());
  }

  @Binds @Singleton abstract TransactionManager transactionManager(TransactionManagerImpl transactionManager);
  @Binds @IntoSet abstract Refreshable refreshable(TransactionManager refreshable);
}
