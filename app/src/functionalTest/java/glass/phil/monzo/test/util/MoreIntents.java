package glass.phil.monzo.test.util;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;

import org.hamcrest.Matcher;

import static android.support.test.espresso.intent.Intents.intending;

public final class MoreIntents {
  public static void withIntents(Runnable test) {
    Intents.init();
    try {
      test.run();
    } finally {
      Intents.release();
    }
  }

  public static void stubOutgoingIntent(Matcher<Intent> matcher) {
    intending(matcher).respondWith(new Instrumentation.ActivityResult(0, null));
  }

  private MoreIntents() {
    throw new AssertionError("No instances");
  }
}
