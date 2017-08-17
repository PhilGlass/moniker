package glass.phil.monzo.presentation.login;

import okhttp3.HttpUrl;

import static glass.phil.monzo.BuildConfig.CLIENT_ID;
import static glass.phil.monzo.BuildConfig.REDIRECT_URL;

final class OauthUrl {
  private static final HttpUrl BASE_OAUTH_URL = new HttpUrl.Builder()
      .scheme("https")
      .host("auth.getmondo.co.uk")
      .addQueryParameter("client_id", CLIENT_ID)
      .addQueryParameter("redirect_uri", REDIRECT_URL)
      .addQueryParameter("response_type", "code")
      .build();

  static HttpUrl withState(String state) {
    return BASE_OAUTH_URL.newBuilder().addQueryParameter("state", state).build();
  }

  private OauthUrl() {
    throw new AssertionError("No instances");
  }
}
