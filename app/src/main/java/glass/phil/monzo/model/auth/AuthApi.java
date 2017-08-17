package glass.phil.monzo.model.auth;

import com.google.auto.value.AutoValue;

import glass.phil.auto.moshi.AutoMoshi;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface AuthApi {
  @FormUrlEncoded
  @POST("oauth2/token") Single<TokenResponse> token(
      @Field("grant_type") String grantType,
      @Field("client_id") String clientId,
      @Field("client_secret") String clientSecret,
      @Field("redirect_uri") String redirectUri,
      @Field("code") String authorizationCode
  );

  @AutoValue @AutoMoshi abstract class TokenResponse {
    abstract String access_token();
    abstract long expires_in();
  }
}
