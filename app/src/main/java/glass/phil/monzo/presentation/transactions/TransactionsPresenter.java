package glass.phil.monzo.presentation.transactions;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import java.util.List;

import javax.inject.Inject;

import glass.phil.monzo.model.balance.BalanceManager;
import glass.phil.monzo.model.balance.BalanceManager.Balance;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.model.transactions.TransactionManager;
import glass.phil.monzo.presentation.base.NoSavedState;
import glass.phil.monzo.presentation.transactions.TransactionsContract.TransactionsView;
import glass.phil.monzo.presentation.transactions.TransactionsContract.ViewModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.util.Arrays.asList;

final class TransactionsPresenter extends TransactionsContract.Presenter {
  private final BalanceManager balanceManager;
  private final TransactionManager transactionManager;

  private final Relay<Action> actions = PublishRelay.create();
  private final Relay<ViewModel> viewModels = BehaviorRelay.create();

  @Inject TransactionsPresenter(BalanceManager balanceManager, TransactionManager transactionManager) {
    this.balanceManager = balanceManager;
    this.transactionManager = transactionManager;
  }

  @Override public void create(@Nullable NoSavedState savedState) {
    final Observable<Update> balance = balanceManager.balance()
        .<Update>map(BalanceUpdate::create)
        .observeOn(AndroidSchedulers.mainThread());
    final Observable<Update> transactions = transactionManager.transactions()
        .<Update>map(TransactionsUpdate::create)
        .observeOn(AndroidSchedulers.mainThread());
    final Observable<Update> fromActions = actions.publish(actions -> Observable.merge(
        actions.filter(it -> it == Action.REFRESH).flatMap(it -> refresh()),
        actions.filter(it -> it == Action.RETRY).flatMap(it -> refresh().startWith(new ErrorDismissedUpdate())),
        actions.filter(it -> it == Action.ERROR_DISMISSED).map(it -> new ErrorDismissedUpdate())
    ));

    disposeOnDestroy(Observable.merge(balance, transactions, fromActions)
        .scan(ViewModel.initialState(), this::reduce)
        .distinctUntilChanged()
        .subscribe(viewModels));
  }

  private Observable<Update> refresh() {
    final Completable balance = balanceManager.refresh().subscribeOn(Schedulers.io());
    final Completable transactions = transactionManager.refresh().subscribeOn(Schedulers.io());
    return Completable.mergeDelayError(asList(balance, transactions))
        .<Boolean>toObservable()
        .concatWith(Observable.just(true))
        .onErrorReturnItem(false)
        .<Update>map(RefreshUpdate::create)
        .observeOn(AndroidSchedulers.mainThread());
  }

  private ViewModel reduce(ViewModel current, Update update) {
    if (update instanceof BalanceUpdate) {
      return current.withBalance(((BalanceUpdate) update).balance());
    } else if (update instanceof TransactionsUpdate) {
      return current.withTransactions(((TransactionsUpdate) update).transactions());
    } else if (update instanceof RefreshUpdate) {
      return current.withError(!((RefreshUpdate) update).success());
    } else if (update instanceof ErrorDismissedUpdate) {
      return current.withError(false);
    } else {
      throw new AssertionError("Unexpected update");
    }
  }

  @Override public void attach(TransactionsView view) {
    disposeOnDetach(viewModels.subscribe(view::render));
    actions.accept(Action.REFRESH);
  }

  @Override public void retry() {
    actions.accept(Action.RETRY);
  }

  @Override public void onErrorDismissed() {
    actions.accept(Action.ERROR_DISMISSED);
  }

  private enum Action {
    REFRESH, RETRY, ERROR_DISMISSED
  }

  private interface Update {}

  @AutoValue static abstract class BalanceUpdate implements Update {
    static BalanceUpdate create(Balance balance) {
      return new AutoValue_TransactionsPresenter_BalanceUpdate(balance);
    }

    abstract Balance balance();
  }

  @AutoValue static abstract class TransactionsUpdate implements Update {
    static TransactionsUpdate create(List<Transaction> transactions) {
      return new AutoValue_TransactionsPresenter_TransactionsUpdate(transactions);
    }

    abstract List<Transaction> transactions();
  }

  @AutoValue static abstract class RefreshUpdate implements Update {
    static RefreshUpdate create(boolean success) {
      return new AutoValue_TransactionsPresenter_RefreshUpdate(success);
    }

    abstract boolean success();
  }

  private static class ErrorDismissedUpdate implements Update {}
}
