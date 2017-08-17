package glass.phil.monzo.test.screens;

import glass.phil.monzo.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class LoginScreen {
  public static void checkDisplayed() {
    onView(withId(R.id.login_screen)).check(matches(isDisplayed()));
  }

  public static void checkShowingForm() {
    onView(withId(R.id.form)).check(matches(isDisplayed()));
  }

  public static void checkShowingError() {
    onView(withId(R.id.error)).check(matches(isDisplayed()));
  }

  public static void clickLogIn() {
    onView(withId(R.id.log_in)).perform(click());
  }

  public static void clickSignUp() {
    onView(withId(R.id.sign_up)).perform(click());
  }

  public static void clickRetry() {
    onView(withId(R.id.retry)).perform(click());
  }

  private LoginScreen() {
    throw new AssertionError("No instances");
  }
}
