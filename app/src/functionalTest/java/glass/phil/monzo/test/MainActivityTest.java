package glass.phil.monzo.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import glass.phil.monzo.FunctionalTestApp;
import glass.phil.monzo.test.screens.LoginScreen;
import glass.phil.monzo.test.screens.TransactionsScreen;
import glass.phil.monzo.test.util.MainActivityRule;
import glass.phil.monzo.test.util.TestHooks;

@RunWith(AndroidJUnit4.class)
public final class MainActivityTest {
  @Rule public final MainActivityRule activityRule = new MainActivityRule();

  @Inject TestHooks testHooks;

  @Before public void setUp() {
    FunctionalTestApp.getTestComponent(InstrumentationRegistry.getTargetContext()).inject(this);
  }

  @Test public void initiallyShowsLoginScreen_ifLoggedOut() {
    testHooks.setLoggedOut();
    activityRule.launchActivity();

    LoginScreen.checkDisplayed();
  }

  @Test public void initiallyShowsTransactionsScreen_ifLoggedIn() {
    testHooks.setLoggedIn();
    activityRule.launchActivity();

    TransactionsScreen.checkDisplayed();
  }

  @Test public void showsLoginScreen_whenReauthenticationRequired() {
    testHooks.setLoggedIn();
    activityRule.launchActivity();

    TransactionsScreen.checkDisplayed();

    testHooks.setLoggedOut().notifyReauthenticationRequired();

    LoginScreen.checkDisplayed();
  }
}
