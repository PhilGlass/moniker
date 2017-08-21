package glass.phil.monzo.presentation.transactions;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import glass.phil.monzo.GlideApp;
import glass.phil.monzo.GlideRequests;

@Module public abstract class TransactionsModule {
  @Provides @Reusable static GlideRequests glide(TransactionsFragment fragment) {
    return GlideApp.with(fragment);
  }

  @Binds abstract TransactionsContract.Presenter presenter(TransactionsPresenter presenter);
}
