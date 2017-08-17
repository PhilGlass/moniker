package glass.phil.monzo;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.test.runner.AndroidJUnitRunner;

import java.util.Locale;

import glass.phil.monzo.test.server.MonzoServer;
import glass.phil.monzo.test.util.Animations;
import glass.phil.monzo.test.util.RxIdlingResource;

public final class FunctionalTestRunner extends AndroidJUnitRunner {
  @Override public void onStart() {
    beforeFirstTest();
    super.onStart();
    afterLastTest();
  }

  private void beforeFirstTest() {
    Animations.disableAll();
    RxIdlingResource.install();
    MonzoServer.getInstance().start();
  }

  private void afterLastTest() {
    MonzoServer.getInstance().stop();
    RxIdlingResource.uninstall();
    Animations.enableAll();
  }

  @SuppressWarnings("deprecation")
  @Override public Application newApplication(ClassLoader cl, String className, Context context) throws
      InstantiationException, IllegalAccessException, ClassNotFoundException {
    final Application application = Instrumentation.newApplication(FunctionalTestApp.class, context);
    final Resources resources = application.getResources();
    final Configuration configuration = resources.getConfiguration();
    configuration.setLocale(Locale.UK);
    resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    return application;
  }
}
