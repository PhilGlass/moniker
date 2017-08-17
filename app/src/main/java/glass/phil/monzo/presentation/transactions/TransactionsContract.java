package glass.phil.monzo.presentation.transactions;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.List;

import glass.phil.monzo.model.balance.BalanceManager.Balance;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.presentation.base.BasePresenter;
import glass.phil.monzo.presentation.base.NoSavedState;

interface TransactionsContract {
  abstract class Presenter extends BasePresenter<TransactionsView, NoSavedState> {
    abstract void retry();
    abstract void onErrorDismissed();
  }

  interface TransactionsView {
    void render(ViewModel model);
  }

  @AutoValue abstract class ViewModel {
    static ViewModel initialState() {
      return new AutoValue_TransactionsContract_ViewModel(null, null, false);
    }

    @Nullable abstract Balance balance();
    @Nullable abstract List<Transaction> transactions();
    abstract boolean error();

    abstract ViewModel withBalance(@Nullable Balance balance);
    abstract ViewModel withTransactions(@Nullable List<Transaction> transactions);
    abstract ViewModel withError(boolean error);
  }
}
