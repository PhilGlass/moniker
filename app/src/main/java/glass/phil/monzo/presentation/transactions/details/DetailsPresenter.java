package glass.phil.monzo.presentation.transactions.details;

import android.support.annotation.Nullable;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.Relay;

import javax.inject.Inject;

import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.model.transactions.TransactionHistory;
import glass.phil.monzo.model.transactions.TransactionManager;
import glass.phil.monzo.presentation.base.NoSavedState;
import glass.phil.monzo.presentation.transactions.details.DetailsContract.DetailsView;
import glass.phil.monzo.presentation.transactions.details.DetailsContract.ViewModel;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

final class DetailsPresenter extends DetailsContract.Presenter {
  private final TransactionManager transactionManager;
  private final Relay<ViewModel> viewModels = BehaviorRelay.createDefault(ViewModel.initialState());

  private final Transaction transaction;

  @Inject DetailsPresenter(TransactionManager transactionManager, Transaction transaction) {
    this.transactionManager = transactionManager;
    this.transaction = transaction;
  }

  @Override public void create(@Nullable NoSavedState savedState) {
    disposeOnDestroy(history()
        .map(ViewModel::just)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(viewModels));
  }

  private Single<Optional<TransactionHistory>> history() {
    if (transaction.topUp()) {
      return transactionManager.topUpHistory();
    } else {
      return transactionManager.merchantHistory(transaction.requireMerchant().id());
    }
  }

  @Override public void attach(DetailsView view) {
    disposeOnDetach(viewModels.subscribe(view::render));
  }
}
