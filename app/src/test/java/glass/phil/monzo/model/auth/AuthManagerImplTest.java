package glass.phil.monzo.model.auth;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;

import java.io.IOException;

import glass.phil.monzo.model.InMemoryStore;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.auth.AuthApi.TokenResponse;
import glass.phil.monzo.model.auth.AuthManager.Token;
import io.reactivex.Single;
import retrofit2.HttpException;

import static glass.phil.monzo.model.StoreAssertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AuthManagerImplTest {
  private static final Instant NOW = Instant.ofEpochSecond(1000);

  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final AuthApi authApi = mock(AuthApi.class);
  private final Clock clock = Clock.fixed(NOW, ZoneId.of("UTC"));
  private final Store<Token> store = new InMemoryStore<>();

  private final AuthManagerImpl authManager = new AuthManagerImpl(authApi, clock, store);

  @Test public void login_withSuccessfulResponse() {
    when(authApi.token(any(), any(), any(), any(), any())).thenReturn(Single.just(tokenResponse("abc", 3600)));

    authManager.login("xyz").test().assertComplete();
    assertThat(store).hasStoredValue(Token.create("abc", NOW.plusSeconds(3600)));
  }

  @Test public void login_withNetworkError() {
    when(authApi.token(any(), any(), any(), any(), any())).thenReturn(Single.error(new IOException()));

    authManager.login("xyz").test().assertError(IOException.class);
    assertThat(store).hasNoStoredValue();
  }

  @Test public void login_withHttpError() {
    when(authApi.token(any(), any(), any(), any(), any())).thenReturn(Single.error(mock(HttpException.class)));

    authManager.login("xyz").test().assertError(HttpException.class);
    assertThat(store).hasNoStoredValue();
  }

  @Test public void currentToken_withNoStoredToken() {
    assertThat(authManager.currentToken()).isNull();
  }

  @Test public void currentToken_withExpiredStoredToken() {
    store.set(Token.create("abc", NOW.minusSeconds(5)));

    assertThat(authManager.currentToken()).isNull();
  }

  @Test public void currentToken_withAlmostExpiredStoredToken() {
    store.set(Token.create("abc", NOW.plusSeconds(5)));

    assertThat(authManager.currentToken()).isNull();
  }

  @Test public void currentToken_withValidStoredToken() {
    store.set(Token.create("abc", NOW.plusSeconds(60)));

    assertThat(authManager.currentToken()).isEqualTo(Token.create("abc", NOW.plusSeconds(60)));
  }

  @Test public void clearToken() {
    store.set(Token.create("abc", NOW.plusSeconds(60)));

    authManager.clearToken();

    assertThat(store).hasNoStoredValue();
  }

  private static TokenResponse tokenResponse(String accessToken, long expiresIn) {
    return new AutoValue_AuthApi_TokenResponse(accessToken, expiresIn);
  }
}
