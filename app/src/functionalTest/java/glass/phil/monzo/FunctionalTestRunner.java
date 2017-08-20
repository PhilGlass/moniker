package glass.phil.monzo;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.squareup.rx2.idler.Rx2Idler;

import glass.phil.monzo.test.server.MonzoServer;
import glass.phil.monzo.test.util.Animations;
import io.reactivex.plugins.RxJavaPlugins;

public final class FunctionalTestRunner extends AndroidJUnitRunner {
  @Override public void onStart() {
    beforeFirstTest();
    super.onStart();
    afterLastTest();
  }

  private void beforeFirstTest() {
    Animations.disableAll();
    RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("io"));
    MonzoServer.getInstance().start();
  }

  private void afterLastTest() {
    Animations.enableAll();
  }

  @Override public Application newApplication(ClassLoader cl, String className, Context context) throws
      InstantiationException, IllegalAccessException, ClassNotFoundException {
    return Instrumentation.newApplication(FunctionalTestApp.class, context);
  }
}
