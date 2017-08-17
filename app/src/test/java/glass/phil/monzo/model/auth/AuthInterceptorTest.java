package glass.phil.monzo.model.auth;

import com.jakewharton.rxrelay2.Relay;
import com.jakewharton.rxrelay2.ReplayRelay;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.threeten.bp.Instant;

import glass.phil.monzo.core.rx.Unit;
import glass.phil.monzo.model.auth.AuthManager.Token;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class AuthInterceptorTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final AuthManager authManager = mock(AuthManager.class);
  private final Relay<Unit> authRelay = ReplayRelay.create();

  private final AuthInterceptor interceptor = new AuthInterceptor(authManager, authRelay);

  private final MockWebServer server = new MockWebServer();
  private final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

  @Before public void setUp() throws Exception {
    server.start();
  }

  @After public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test public void noToken() throws Exception {
    when(authManager.currentToken()).thenReturn(null);

    assertThat(executeRequest(new MockResponse()).getHeader("Authorization")).isNull();
  }

  @Test public void withToken() throws Exception {
    when(authManager.currentToken()).thenReturn(Token.create("abc", Instant.EPOCH));

    assertThat(executeRequest(new MockResponse()).getHeader("Authorization")).isEqualTo("Bearer abc");
  }

  @Test public void successfulResponse() throws Exception {
    executeRequest(new MockResponse().setResponseCode(200));

    verify(authManager, never()).clearToken();
    authRelay.test().assertNoValues();
  }

  @Test public void unauthorizedResponse() throws Exception {
    executeRequest(new MockResponse().setResponseCode(401));

    verify(authManager).clearToken();
    authRelay.test().assertValue(Unit.INSTANCE);
  }

  @Test public void serverErrorResponse() throws Exception {
    executeRequest(new MockResponse().setResponseCode(500));

    verify(authManager, never()).clearToken();
    authRelay.test().assertNoValues();
  }

  private RecordedRequest executeRequest(MockResponse response) throws Exception {
    server.enqueue(response);
    client.newCall(new Request.Builder().url(server.url("/")).build()).execute();
    return server.takeRequest();
  }
}
