package glass.phil.monzo.presentation.transactions.details;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.model.transactions.Transaction;

@Module public abstract class DetailsModule {
  @Provides static Transaction transaction(DetailsFragment fragment) {
    return fragment.transaction();
  }

  @Binds abstract DetailsContract.Presenter presenter(DetailsPresenter presenter);
}
