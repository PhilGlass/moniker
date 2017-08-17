package glass.phil.monzo;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

public final class RxSchedulerOverrideRule implements TestRule {
  private final TestScheduler mainThreadScheduler = new TestScheduler();

  @Override public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        try {
          RxJavaPlugins.setIoSchedulerHandler(original -> Schedulers.trampoline());
          // Like AndroidSchedulers.mainThread(), a TestScheduler will never execute scheduled actions synchronously -
          // they are always placed in a run queue. Any actions that are scheduled to run at or before the scheduler's
          // current time can be executed through a call to executePendingMainThreadActions().
          // The init hook is used to prevent RxJava ever loading the real main thread scheduler, as doing this on a
          // desktop JVM will result in a crash (through calls to unstubbed methods in the mockable android.jar).
          // The dynamic hook is used to supply a new TestScheduler for each instance of this rule,
          // ensuring tests do not interfere with one another.
          RxAndroidPlugins.setInitMainThreadSchedulerHandler(callable -> mainThreadScheduler);
          RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> mainThreadScheduler);
          base.evaluate();
        } finally {
          RxJavaPlugins.reset();
          RxAndroidPlugins.reset();
        }
      }
    };
  }

  public void executePendingMainThreadActions() {
    mainThreadScheduler.triggerActions();
  }
}
