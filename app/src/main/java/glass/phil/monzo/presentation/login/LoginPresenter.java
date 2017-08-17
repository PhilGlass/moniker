package glass.phil.monzo.presentation.login;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import glass.phil.monzo.core.Generator;
import glass.phil.monzo.core.Objects;
import glass.phil.monzo.model.Refreshable;
import glass.phil.monzo.model.auth.AuthManager;
import glass.phil.monzo.presentation.login.LoginContract.LoginView;
import glass.phil.monzo.presentation.login.LoginContract.SavedState;
import glass.phil.monzo.presentation.login.LoginContract.ViewModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

final class LoginPresenter extends LoginContract.Presenter {
  private final AuthManager authManager;
  private final Generator<String> stateGenerator;
  private final Set<Refreshable> refreshables;

  private final Relay<Action> actions = PublishRelay.create();
  private final BehaviorRelay<ViewModel> viewModels = BehaviorRelay.create();

  @Inject LoginPresenter(AuthManager authManager, Generator<String> stateGenerator, Set<Refreshable> refreshables) {
    this.authManager = authManager;
    this.stateGenerator = stateGenerator;
    this.refreshables = refreshables;
  }

  @Override public void create(@Nullable SavedState savedState) {
    final ViewModel initialState;
    if (savedState == null) {
      initialState = ViewModel.just(stateGenerator.generate()).withLoginComplete(loggedIn());
    } else {
      initialState = ViewModel.just(savedState.state())
          .withError(savedState.error())
          .withLoginComplete(loggedIn());
    }

    final Observable<Update> updates = actions.publish(actions -> Observable.merge(
        actions.ofType(LoginAction.class).flatMap(this::tryLogin),
        actions.ofType(RetryAction.class).map(it -> new RetryUpdate())
    ));

    disposeOnDestroy(updates.scan(initialState, this::reduce).subscribe(viewModels));
  }

  // Check whether we're already logged in. This guards against the case where the user initiates a login, navigates
  // away while the login request is in flight and returns some time later, after our app's process has been killed.
  private boolean loggedIn() {
    return authManager.currentToken() != null;
  }

  private Observable<Update> tryLogin(LoginAction loginAction) {
    final Single<String> latestState = viewModels.firstOrError().map(ViewModel::state);
    return latestState.flatMapObservable(state -> {
      if (!Objects.equal(state, loginAction.state())) {
        return Observable.just(LoginResultUpdate.successful(false));
      }
      return authManager.login(loginAction.code())
          .andThen(loadInitialData())
          .<Update>toObservable()
          .concatWith(Observable.just(LoginResultUpdate.successful(true)))
          .onErrorReturnItem(LoginResultUpdate.successful(false))
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .startWith(new LoginInProgressUpdate());
    });
  }

  @SuppressWarnings("Convert2streamapi")
  private Completable loadInitialData() {
    final List<Completable> completables = new ArrayList<>(refreshables.size());
    for (Refreshable refreshable : refreshables) {
      completables.add(refreshable.refresh().subscribeOn(Schedulers.io()));
    }
    return Completable.merge(completables);
  }

  private ViewModel reduce(ViewModel current, Update update) {
    if (update instanceof LoginInProgressUpdate) {
      return current.withLoading(true);
    } else if (update instanceof LoginResultUpdate) {
      if (((LoginResultUpdate) update).success()) {
        return current.withLoading(false).withError(false).withLoginComplete(true);
      }
      return current.withLoading(false).withError(true);
    } else if (update instanceof RetryUpdate) {
      return current.withState(stateGenerator.generate()).withError(false);
    } else {
      throw new AssertionError("Unexpected update");
    }
  }

  @Override public void attach(LoginView view) {
    disposeOnDetach(viewModels.subscribe(view::render));
  }

  @Override @Nullable public SavedState saveState() {
    final ViewModel currentModel = viewModels.getValue();
    return currentModel == null ? null : SavedState.create(currentModel.state(), currentModel.error());
  }

  @Override void onLoginResult(String code, String state) {
    actions.accept(LoginAction.create(code, state));
  }

  @Override void retry() {
    actions.accept(new RetryAction());
  }

  private interface Action {}

  @AutoValue static abstract class LoginAction implements Action {
    static LoginAction create(String code, String state) {
      return new AutoValue_LoginPresenter_LoginAction(code, state);
    }

    abstract String code();
    abstract String state();
  }

  private static class RetryAction implements Action {}

  private interface Update {}

  private static class LoginInProgressUpdate implements Update {}

  @AutoValue static abstract class LoginResultUpdate implements Update {
    static LoginResultUpdate successful(boolean success) {
      return new AutoValue_LoginPresenter_LoginResultUpdate(success);
    }

    abstract boolean success();
  }

  private static class RetryUpdate implements Update {}
}
