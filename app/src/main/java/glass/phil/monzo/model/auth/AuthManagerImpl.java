package glass.phil.monzo.model.auth;

import android.support.annotation.Nullable;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;

import javax.inject.Inject;

import glass.phil.monzo.model.Clocks.Utc;
import glass.phil.monzo.model.Store;
import io.reactivex.Completable;

import static glass.phil.monzo.BuildConfig.CLIENT_ID;
import static glass.phil.monzo.BuildConfig.CLIENT_SECRET;
import static glass.phil.monzo.BuildConfig.REDIRECT_URL;

final class AuthManagerImpl implements AuthManager {
  private final AuthApi api;
  private final Clock clock;
  private final Store<Token> store;

  @Inject AuthManagerImpl(AuthApi api, @Utc Clock clock, Store<Token> store) {
    this.api = api;
    this.clock = clock;
    this.store = store;
  }

  @Override public Completable login(String code) {
    return api.token("authorization_code", CLIENT_ID, CLIENT_SECRET, REDIRECT_URL, code)
        .doOnSuccess(response -> {
          final Instant expiresAt = clock.instant().plusSeconds(response.expires_in());
          store.set(Token.create(response.access_token(), expiresAt));
        }).toCompletable();
  }

  @Override @Nullable public Token currentToken() {
    final Token stored = store.get();
    return (stored == null || expiredOrExpiringSoon(stored)) ? null : stored;
  }

  private boolean expiredOrExpiringSoon(Token token) {
    return token.expiresAt().minusSeconds(15).isBefore(clock.instant());
  }

  @Override public void clearToken() {
    store.set(null);
  }
}
