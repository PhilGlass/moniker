package glass.phil.monzo.test;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import glass.phil.monzo.BuildConfig;
import glass.phil.monzo.FunctionalTestApp;
import glass.phil.monzo.TestData;
import glass.phil.monzo.presentation.MainActivity;
import glass.phil.monzo.presentation.login.RecordingBrowser;
import glass.phil.monzo.test.screens.LoginScreen;
import glass.phil.monzo.test.screens.TransactionsScreen;
import glass.phil.monzo.test.server.MonzoServer;
import glass.phil.monzo.test.server.ResetServerRule;
import glass.phil.monzo.test.util.MainActivityRule;
import glass.phil.monzo.test.util.MoreIntents;
import glass.phil.monzo.test.util.TestHooks;
import okhttp3.HttpUrl;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static glass.phil.monzo.test.util.MoreIntents.withIntents;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {
  @Rule public final MainActivityRule activityRule = new MainActivityRule();
  @Rule public final ResetServerRule serverRule = new ResetServerRule();

  @Inject TestHooks testHooks;

  @Before public void setUp() {
    FunctionalTestApp.getTestComponent(InstrumentationRegistry.getTargetContext()).inject(this);
    testHooks.setLoggedOut();
    activityRule.launchActivity();
  }

  @Test public void initiallyShowsLoginForm() {
    LoginScreen.checkDisplayed();
    LoginScreen.checkShowingForm();
  }

  @Test public void clickingLogIn_opensMonzoLoginPage() {
    LoginScreen.clickLogIn();

    final HttpUrl expectedUrl = new HttpUrl.Builder()
        .scheme("https")
        .host("auth.getmondo.co.uk")
        .addQueryParameter("client_id", BuildConfig.CLIENT_ID)
        .addQueryParameter("redirect_uri", BuildConfig.REDIRECT_URL)
        .addQueryParameter("response_type", "code")
        .addQueryParameter("state", TestData.STATE)
        .build();
    RecordingBrowser.getInstance().assertPageShown(expectedUrl);
  }

  @Test public void clickingSignUp_opensMonzoStoreListing() {
    withIntents(() -> {
      final Matcher<Intent> matcher = allOf(
          hasAction(Intent.ACTION_VIEW),
          hasData("market://details?id=co.uk.getmondo")
      );

      // The device/emulator running this test may not include an app that can handle market links.
      MoreIntents.stubOutgoingIntent(matcher);

      LoginScreen.clickSignUp();

      Intents.intended(matcher);
    });
  }

  @Test public void successfulLogin_navigatesToTransactionsScreen() {
    sendLoginResult(TestData.CODE, TestData.STATE);

    TransactionsScreen.checkDisplayed();
  }

  @Test public void loginWithIncorrectState_showsError() {
    sendLoginResult(TestData.CODE, TestData.STATE + "_");

    LoginScreen.checkShowingError();
  }

  @Test public void loginWithFailedTokenRequest_showsError() {
    MonzoServer.getInstance().onTokenRequestReturnError();

    sendLoginResult(TestData.CODE, TestData.STATE);

    LoginScreen.checkShowingError();
  }

  @Test public void loginWithFailedTransactionsRequest_showsError() {
    MonzoServer.getInstance().onTransactionsRequestReturnError();

    sendLoginResult(TestData.CODE, TestData.STATE);

    LoginScreen.checkShowingError();
  }

  @Test public void clickingRetry_showsLoginForm() {
    MonzoServer.getInstance().onTokenRequestReturnError();

    sendLoginResult(TestData.CODE, TestData.STATE);
    LoginScreen.clickRetry();

    LoginScreen.checkShowingForm();
  }

  private void sendLoginResult(String code, String state) {
    final Uri redirectUri = Uri.parse(BuildConfig.REDIRECT_URL).buildUpon()
        .appendQueryParameter("code", code)
        .appendQueryParameter("state", state)
        .build();
    final Intent intent = new Intent(Intent.ACTION_VIEW)
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .addCategory(Intent.CATEGORY_DEFAULT)
        .setData(redirectUri)
        .setClassName(InstrumentationRegistry.getTargetContext(), MainActivity.class.getName());
    activityRule.getActivity().startActivity(intent);
  }
}
