package glass.phil.monzo.test.util;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;

import io.reactivex.plugins.RxJavaPlugins;

public final class RxIdlingResource {
  private static final CountingIdlingResource COUNTING_RESOURCE = new CountingIdlingResource("RxJavaScheduledTasks");

  public static void install() {
    Espresso.registerIdlingResources(COUNTING_RESOURCE);
    RxJavaPlugins.setScheduleHandler(task -> () -> {
      COUNTING_RESOURCE.increment();
      try {
        task.run();
      } finally {
        COUNTING_RESOURCE.decrement();
      }
    });
  }

  private RxIdlingResource() {
    throw new AssertionError("No instances.");
  }
}
