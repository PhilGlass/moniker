package glass.phil.monzo.presentation.transactions;

import dagger.Binds;
import dagger.Module;

@Module public abstract class TransactionsModule {
  @Binds abstract TransactionsContract.Presenter presenter(TransactionsPresenter presenter);
}
