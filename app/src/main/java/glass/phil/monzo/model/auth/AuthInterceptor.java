package glass.phil.monzo.model.auth;

import android.support.annotation.NonNull;

import com.jakewharton.rxrelay2.Relay;

import java.io.IOException;

import javax.inject.Inject;

import glass.phil.monzo.core.rx.Unit;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AuthInterceptor implements Interceptor {
  private final AuthManager authManager;
  private final Relay<Unit> authRelay;

  @Inject AuthInterceptor(AuthManager authManager, Relay<Unit> authRelay) {
    this.authManager = authManager;
    this.authRelay = authRelay;
  }

  @Override public Response intercept(@NonNull Chain chain) throws IOException {
    Request request = chain.request();
    final AuthManager.Token token = authManager.currentToken();
    if (token != null) {
      request = request.newBuilder().addHeader("Authorization", "Bearer " + token.accessToken()).build();
    }

    final Response response = chain.proceed(request);
    if (response.code() == 401) {
      authManager.clearToken();
      authRelay.accept(Unit.INSTANCE);
    }
    return response;
  }
}
