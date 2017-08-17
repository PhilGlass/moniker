package glass.phil.monzo.model.auth;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.threeten.bp.Instant;

import glass.phil.auto.moshi.AutoMoshi;
import io.reactivex.Completable;

public interface AuthManager {
  Completable login(String code);

  @Nullable Token currentToken();

  void clearToken();

  @AutoValue @AutoMoshi abstract class Token {
    public abstract String accessToken();
    public abstract Instant expiresAt();

    public static Token create(String accessToken, Instant expiresAt) {
      return new AutoValue_AuthManager_Token(accessToken, expiresAt);
    }
  }
}
