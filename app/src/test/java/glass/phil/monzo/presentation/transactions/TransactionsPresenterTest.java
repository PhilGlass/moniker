package glass.phil.monzo.presentation.transactions;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glass.phil.monzo.model.balance.BalanceManager;
import glass.phil.monzo.model.balance.BalanceManager.Balance;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.model.transactions.TransactionManager;
import glass.phil.monzo.presentation.base.PresenterTest;
import glass.phil.monzo.presentation.transactions.TransactionsContract.TransactionsView;
import glass.phil.monzo.presentation.transactions.TransactionsContract.ViewModel;
import io.reactivex.Completable;

import static glass.phil.monzo.TestData.balance;
import static glass.phil.monzo.model.transactions.TestTransactions.topUp;
import static glass.phil.monzo.presentation.transactions.TransactionsContract.ViewModel.initialState;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TransactionsPresenterTest extends PresenterTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final BalanceManager balanceManager = mock(BalanceManager.class);
  private final TransactionManager transactionManager = mock(TransactionManager.class);

  private final Relay<Balance> balanceUpdates = PublishRelay.create();
  private final Relay<List<Transaction>> transactionsUpdates = PublishRelay.create();
  private final RecordingTransactionsView view = new RecordingTransactionsView();

  private final TransactionsPresenter presenter = new TransactionsPresenter(balanceManager, transactionManager);

  @Before public void setUp() {
    when(balanceManager.balance()).thenReturn(balanceUpdates);
    when(balanceManager.refresh()).thenReturn(Completable.never());
    when(transactionManager.transactions()).thenReturn(transactionsUpdates);
    when(transactionManager.refresh()).thenReturn(Completable.never());
  }

  @Test public void noUpdates() {
    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        initialState()
    );
  }

  @Test public void updatePreAttach() {
    performCreate(presenter);
    emitTransactionsUpdate(emptyList());
    executePendingMainThreadActions();
    performAttach(presenter, view);

    view.assertReceivedExactly(
        initialState().withTransactions(emptyList())
    );
  }

  @Test public void transactionsUpdate() {
    performCreate(presenter);
    performAttach(presenter, view);
    emitTransactionsUpdate(emptyList());
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList())
    );
  }

  @Test public void balanceUpdate() {
    performCreate(presenter);
    performAttach(presenter, view);
    emitBalanceUpdate(balance(100_00, 50_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withBalance(balance(100_00, 50_00))
    );
  }

  @Test public void transactionsAndBalanceUpdates() {
    performCreate(presenter);
    performAttach(presenter, view);
    emitTransactionsUpdate(emptyList());
    executePendingMainThreadActions();
    emitBalanceUpdate(balance(100_00, 50_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList()),
        initialState().withTransactions(emptyList()).withBalance(balance(100_00, 50_00))
    );
  }

  @Test public void multipleTransactionsUpdates() {
    final Transaction transaction = topUp().withAmount(+25_00).toTransaction();
    performCreate(presenter);
    performAttach(presenter, view);
    emitTransactionsUpdate(emptyList());
    emitTransactionsUpdate(singletonList(transaction));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList()),
        initialState().withTransactions(singletonList(transaction))
    );
  }

  @Test public void multipleBalanceUpdates() {
    performCreate(presenter);
    performAttach(presenter, view);
    emitBalanceUpdate(balance(100_00, 50_00));
    emitBalanceUpdate(balance(50_00, 25_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withBalance(balance(100_00, 50_00)),
        initialState().withBalance(balance(50_00, 25_00))
    );
  }

  @Test public void refreshSuccessful() {
    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.complete());

    performCreate(presenter);
    performAttach(presenter, view);
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState()
    );
  }

  @Test public void refreshFailed() {
    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.error(new IOException()));

    performCreate(presenter);
    performAttach(presenter, view);
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true)
    );
  }

  @Test public void detachAndAttachToSameView() {
    performCreate(presenter);
    performAttach(presenter, view);
    emitTransactionsUpdate(emptyList());
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList())
    );

    performDetach(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList()),
        initialState().withTransactions(emptyList())
    );
  }

  @Test public void detachAndAttachToNewView() {
    final RecordingTransactionsView newView = new RecordingTransactionsView();

    performCreate(presenter);
    performAttach(presenter, view);
    emitTransactionsUpdate(emptyList());
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList())
    );

    performDetach(presenter);
    performAttach(presenter, newView);

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList())
    );
    newView.assertReceivedExactly(
        initialState().withTransactions(emptyList())
    );
  }

  @Test public void updateWhileDetached() {
    performCreate(presenter);
    performAttach(presenter, view);
    performDetach(presenter);
    emitTransactionsUpdate(emptyList());
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState()
    );

    performAttach(presenter, view);

    view.assertReceivedExactly(
        initialState(),
        initialState().withTransactions(emptyList())
    );
  }

  @Test public void retryWithSuccessfulRefresh() {
    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.error(new IOException()));
    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        initialState()
    );

    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true)
    );

    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.complete());
    presenter.retry();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true),
        initialState().withError(false)
    );

    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true),
        initialState().withError(false)
    );
  }

  @Test public void retryWithFailedRefresh() {
    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.error(new IOException()));

    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        initialState()
    );

    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true)
    );

    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.error(new IOException()));
    presenter.retry();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true),
        initialState().withError(false)
    );

    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true),
        initialState().withError(false),
        initialState().withError(true)
    );
  }

  @Test public void errorDismissed() {
    when(balanceManager.refresh()).thenReturn(Completable.complete());
    when(transactionManager.refresh()).thenReturn(Completable.error(new IOException()));

    performCreate(presenter);
    performAttach(presenter, view);
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true)
    );

    presenter.onErrorDismissed();

    view.assertReceivedExactly(
        initialState(),
        initialState().withError(true),
        initialState().withError(false)
    );
  }

  @Test public void destroy() {
    performCreate(presenter);

    assertThat(balanceUpdates.hasObservers()).isTrue();
    assertThat(balanceUpdates.hasObservers()).isTrue();

    performDestroy(presenter);

    assertThat(balanceUpdates.hasObservers()).isFalse();
    assertThat(balanceUpdates.hasObservers()).isFalse();
  }

  private void emitTransactionsUpdate(List<Transaction> transactions) {
    transactionsUpdates.accept(transactions);
  }

  private void emitBalanceUpdate(Balance balance) {
    balanceUpdates.accept(balance);
  }

  private static class RecordingTransactionsView implements TransactionsView {
    private final List<ViewModel> renderedViewModels = new ArrayList<>();

    @Override public void render(ViewModel model) {
      renderedViewModels.add(model);
    }

    void assertReceivedExactly(ViewModel... expectedModels) {
      assertThat(renderedViewModels).containsExactly(expectedModels);
    }
  }
}
