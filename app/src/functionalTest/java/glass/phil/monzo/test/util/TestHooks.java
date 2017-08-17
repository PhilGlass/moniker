package glass.phil.monzo.test.util;

import com.jakewharton.rxrelay2.Relay;

import org.threeten.bp.Instant;

import java.util.Set;

import javax.inject.Inject;

import glass.phil.monzo.TestData;
import glass.phil.monzo.core.rx.Unit;
import glass.phil.monzo.model.Refreshable;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.auth.AuthManager.Token;
import io.reactivex.schedulers.Schedulers;

public final class TestHooks {
  private final Store<Token> tokenStore;
  private final Relay<Unit> authRelay;
  private final Set<Refreshable> refreshables;

  @Inject TestHooks(Store<Token> tokenStore, Relay<Unit> authRelay, Set<Refreshable> refreshables) {
    this.tokenStore = tokenStore;
    this.authRelay = authRelay;
    this.refreshables = refreshables;
  }

  public TestHooks setLoggedIn() {
    tokenStore.set(Token.create(TestData.ACCESS_TOKEN, Instant.MAX));
    return this;
  }

  public TestHooks setLoggedOut() {
    tokenStore.set(null);
    return this;
  }

  public TestHooks notifyReauthenticationRequired() {
    authRelay.accept(Unit.INSTANCE);
    return this;
  }

  public TestHooks triggerRefresh() {
    for (Refreshable refreshable : refreshables) {
      refreshable.refresh().subscribeOn(Schedulers.io()).subscribe(() -> {}, error -> {});
    }
    return this;
  }
}
