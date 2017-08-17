package glass.phil.monzo.presentation.transactions.details;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.model.transactions.TransactionHistory;
import glass.phil.monzo.presentation.base.BasePresenter;
import glass.phil.monzo.presentation.base.NoSavedState;

interface DetailsContract {
  abstract class Presenter extends BasePresenter<DetailsView, NoSavedState> {}

  interface DetailsView {
    void render(ViewModel model);
  }

  @AutoValue abstract class ViewModel {
    static ViewModel initialState() {
      return create(true, null);
    }

    static ViewModel just(Optional<TransactionHistory> history) {
      return create(false, history.value());
    }

    private static ViewModel create(boolean loading, @Nullable TransactionHistory history) {
      return new AutoValue_DetailsContract_ViewModel(loading, history);
    }

    abstract boolean loading();
    @Nullable abstract TransactionHistory history();
  }
}
