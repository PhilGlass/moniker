package glass.phil.monzo.presentation.transactions.details;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.model.transactions.TestTransaction;
import glass.phil.monzo.model.transactions.TransactionHistory;
import glass.phil.monzo.model.transactions.TransactionManager;
import glass.phil.monzo.presentation.base.PresenterTest;
import glass.phil.monzo.presentation.transactions.details.DetailsContract.ViewModel;
import io.reactivex.subjects.SingleSubject;

import static glass.phil.monzo.TestData.history;
import static glass.phil.monzo.model.transactions.TestTransactions.ASDA;
import static glass.phil.monzo.model.transactions.TestTransactions.atMerchant;
import static glass.phil.monzo.model.transactions.TestTransactions.topUp;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DetailsPresenterTest extends PresenterTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final TransactionManager transactionManager = mock(TransactionManager.class);

  private final SingleSubject<Optional<TransactionHistory>> historyRequest = SingleSubject.create();
  private final RecordingDetailsView view = new RecordingDetailsView();

  @Test public void updatePreAttach() {
    when(transactionManager.topUpHistory()).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(topUp());

    performCreate(presenter);
    historyRequest.onSuccess(history(2, +20_00, +40_00));
    executePendingMainThreadActions();
    performAttach(presenter, view);

    view.assertReceivedExactly(
        ViewModel.just(history(2, +20_00, +40_00))
    );
  }

  @Test public void topUpHistory() {
    when(transactionManager.topUpHistory()).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(topUp());

    performCreate(presenter);
    performAttach(presenter, view);
    historyRequest.onSuccess(history(2, +20_00, +40_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, +20_00, +40_00))
    );
  }

  @Test public void merchantHistory() {
    when(transactionManager.merchantHistory(ASDA.id())).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(atMerchant(ASDA));

    performCreate(presenter);
    performAttach(presenter, view);
    historyRequest.onSuccess(history(2, -50_00, -100_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, -50_00, -100_00))
    );
  }

  @Test public void noMerchantHistory() {
    when(transactionManager.merchantHistory(ASDA.id())).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(atMerchant(ASDA));

    performCreate(presenter);
    performAttach(presenter, view);
    historyRequest.onSuccess(Optional.none());
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(Optional.none())
    );
  }

  @Test public void detachAndAttachToSameView() {
    when(transactionManager.topUpHistory()).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(topUp());

    performCreate(presenter);
    performAttach(presenter, view);
    historyRequest.onSuccess(history(2, +20_00, +40_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, +20_00, +40_00))
    );

    performDetach(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, +20_00, +40_00)),
        ViewModel.just(history(2, +20_00, +40_00))
    );
  }

  @Test public void detachAndAttachToNewView() {
    when(transactionManager.topUpHistory()).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(topUp());
    final RecordingDetailsView newView = new RecordingDetailsView();

    performCreate(presenter);
    performAttach(presenter, view);
    historyRequest.onSuccess(history(2, +20_00, +40_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, +20_00, +40_00))
    );

    performDetach(presenter);
    performAttach(presenter, newView);

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, +20_00, +40_00))
    );
    newView.assertReceivedExactly(
        ViewModel.just(history(2, +20_00, +40_00))
    );
  }

  @Test public void updateWhileDetached() {
    when(transactionManager.topUpHistory()).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(topUp());

    performCreate(presenter);
    performAttach(presenter, view);
    performDetach(presenter);
    historyRequest.onSuccess(history(2, +20_00, +40_00));
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        ViewModel.initialState()
    );

    performAttach(presenter, view);

    view.assertReceivedExactly(
        ViewModel.initialState(),
        ViewModel.just(history(2, +20_00, +40_00))
    );
  }

  @Test public void destroy() {
    when(transactionManager.topUpHistory()).thenReturn(historyRequest);
    final DetailsPresenter presenter = createPresenter(topUp());

    performCreate(presenter);
    assertThat(historyRequest.hasObservers()).isTrue();

    performDestroy(presenter);
    assertThat(historyRequest.hasObservers()).isFalse();
  }

  private DetailsPresenter createPresenter(TestTransaction transaction) {
    return new DetailsPresenter(transactionManager, transaction.toTransaction());
  }

  private static class RecordingDetailsView implements DetailsContract.DetailsView {
    private final List<ViewModel> renderedViewModels = new ArrayList<>();

    @Override public void render(ViewModel model) {
      renderedViewModels.add(model);
    }

    void assertReceivedExactly(ViewModel... expectedModels) {
      assertThat(renderedViewModels).containsExactly(expectedModels);
    }
  }
}
