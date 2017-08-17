package glass.phil.monzo.presentation.login;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.threeten.bp.Instant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import glass.phil.monzo.core.Generator;
import glass.phil.monzo.core.Sets;
import glass.phil.monzo.model.Refreshable;
import glass.phil.monzo.model.auth.AuthManager;
import glass.phil.monzo.model.auth.AuthManager.Token;
import glass.phil.monzo.presentation.base.PresenterTest;
import glass.phil.monzo.presentation.login.LoginContract.SavedState;
import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;

import static glass.phil.monzo.presentation.login.LoginContract.ViewModel.just;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public final class LoginPresenterTest extends PresenterTest {
  private static final String GENERATED_STATE = "47f9d910-fb9a-4e5f-808c-c84a9484cd66";
  private static final String SECOND_GENERATED_STATE = "47f9d910-fb9a-4e5f-808c-c84a9484cd67";
  private static final String RESTORED_STATE = "94e8caf4-9cff-45b8-8dc4-9f679f3c0814";
  private static final String WRONG_STATE = "94e8caf4-9cff-45b8-8dc4-9f679f3c0815";
  private static final String CODE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final AuthManager authManager = Mockito.mock(AuthManager.class);
  private final StubStateGenerator stateGenerator = new StubStateGenerator();

  private final CompletableSubject balanceRefreshRequest = CompletableSubject.create();
  private final CompletableSubject transactionsRefreshRequest = CompletableSubject.create();
  private final RecordingLoginView view = new RecordingLoginView();

  private LoginPresenter presenter;

  @Before public void setUp() {
    final Set<Refreshable> refreshables = Sets.newHashSet(
        () -> balanceRefreshRequest,
        () -> transactionsRefreshRequest
    );
    presenter = new LoginPresenter(authManager, stateGenerator, refreshables);
  }

  @Test public void noRestoredState() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE)
    );
  }

  @Test public void restoredState() {
    performCreate(presenter, SavedState.create(RESTORED_STATE, false));
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(RESTORED_STATE)
    );
  }

  @Test public void restoredStateWithError() {
    performCreate(presenter, SavedState.create(RESTORED_STATE, true));
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(RESTORED_STATE).withError(true)
    );
  }

  @Test public void alreadyLoggedIn() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.currentToken()).thenReturn(Token.create("abc", Instant.MAX));

    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE).withLoginComplete(true)
    );
  }

  @Test public void loginResultWithSuccessfulLogin() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.login(CODE)).thenReturn(Completable.complete());

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true)
    );

    balanceRefreshRequest.onComplete();
    transactionsRefreshRequest.onComplete();
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true),
        just(GENERATED_STATE).withLoginComplete(true)
    );
  }

  @Test public void loginResultWithWrongState() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, WRONG_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withError(true)
    );
  }

  @Test public void loginResultWithFailedTokenRequest() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.login(CODE)).thenReturn(Completable.error(new IOException()));

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true)
    );

    executePendingMainThreadActions();

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true),
        just(GENERATED_STATE).withError(true)
    );
  }

  @Test public void loginResultWithFailedBalanceRefresh() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.login(CODE)).thenReturn(Completable.complete());

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true)
    );

    balanceRefreshRequest.onError(new IOException());
    transactionsRefreshRequest.onComplete();
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true),
        just(GENERATED_STATE).withError(true)
    );
  }

  @Test public void loginResultWithFailedTransactionsRefresh() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.login(CODE)).thenReturn(Completable.complete());

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true)
    );

    balanceRefreshRequest.onComplete();
    transactionsRefreshRequest.onError(new IOException());
    executePendingMainThreadActions();

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true),
        just(GENERATED_STATE).withError(true)
    );
  }

  @Test public void retry() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, WRONG_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withError(true)
    );

    stateGenerator.setNextState(SECOND_GENERATED_STATE);
    presenter.retry();

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withError(true),
        just(SECOND_GENERATED_STATE)
    );
  }

  @Test public void detachAndAttachWhileShowingForm() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE)
    );

    stateGenerator.setNextState(SECOND_GENERATED_STATE);
    performDetach(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE)
    );
  }

  @Test public void detachAndAttachWhileShowingError() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, WRONG_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withError(true)
    );

    stateGenerator.setNextState(SECOND_GENERATED_STATE);
    performDetach(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withError(true),
        just(GENERATED_STATE).withError(true)
    );
  }

  @Test public void detachAndAttachToNewView() {
    final RecordingLoginView newView = new RecordingLoginView();
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE)
    );

    stateGenerator.setNextState(SECOND_GENERATED_STATE);
    performDetach(presenter);
    performAttach(presenter, newView);

    view.assertReceivedExactly(
        just(GENERATED_STATE)
    );
    newView.assertReceivedExactly(
        just(GENERATED_STATE)
    );
  }

  @Test public void successfulLoginWhileDetached() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.login(CODE)).thenReturn(Completable.complete());

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true)
    );

    performDetach(presenter);
    balanceRefreshRequest.onComplete();
    transactionsRefreshRequest.onComplete();
    executePendingMainThreadActions();
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true),
        just(GENERATED_STATE).withLoginComplete(true)
    );
  }

  @Test public void failedLoginWhileDetached() {
    stateGenerator.setNextState(GENERATED_STATE);
    when(authManager.login(CODE)).thenReturn(Completable.complete());

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true)
    );

    performDetach(presenter);
    balanceRefreshRequest.onComplete();
    transactionsRefreshRequest.onError(new IOException());
    executePendingMainThreadActions();
    performAttach(presenter, view);

    view.assertReceivedExactly(
        just(GENERATED_STATE),
        just(GENERATED_STATE).withLoading(true),
        just(GENERATED_STATE).withError(true)
    );
  }

  @Test public void saveState() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);

    assertThat(presenter.saveState()).isEqualTo(SavedState.create(GENERATED_STATE, false));
  }

  @Test public void saveStateWithError() {
    stateGenerator.setNextState(GENERATED_STATE);

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, WRONG_STATE);

    assertThat(presenter.saveState()).isEqualTo(SavedState.create(GENERATED_STATE, true));
  }

  @Test public void destroy() {
    stateGenerator.setNextState(GENERATED_STATE);
    final CompletableSubject loginRequest = CompletableSubject.create();
    when(authManager.login(CODE)).thenReturn(loginRequest);

    performCreate(presenter);
    performAttach(presenter, view);
    presenter.onLoginResult(CODE, GENERATED_STATE);

    assertThat(loginRequest.hasObservers()).isTrue();

    performDetach(presenter);
    performDestroy(presenter);

    assertThat(loginRequest.hasObservers()).isFalse();
  }

  private static class StubStateGenerator implements Generator<String> {
    private volatile String nextState;

    @Override public String generate() {
      return nextState;
    }

    void setNextState(String nextState) {
      this.nextState = nextState;
    }
  }

  private static class RecordingLoginView implements LoginContract.LoginView {
    private final List<LoginContract.ViewModel> renderedViewModels = new ArrayList<>();

    @Override public void render(LoginContract.ViewModel model) {
      renderedViewModels.add(model);
    }

    void assertReceivedExactly(LoginContract.ViewModel... expectedModels) {
      assertThat(renderedViewModels).containsExactly(expectedModels);
    }
  }
}
