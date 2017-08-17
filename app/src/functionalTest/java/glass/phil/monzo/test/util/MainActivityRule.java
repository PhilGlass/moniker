package glass.phil.monzo.test.util;

import android.support.test.rule.ActivityTestRule;

import glass.phil.monzo.presentation.MainActivity;

public final class MainActivityRule extends ActivityTestRule<MainActivity> {
  public MainActivityRule() {
    super(MainActivity.class, true, false);
  }

  public void launchActivity() {
    launchActivity(null);
  }
}
