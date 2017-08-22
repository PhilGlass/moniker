package glass.phil.monzo;

import android.content.Context;

import java.util.Locale;

import dagger.android.AndroidInjector;

public final class FunctionalTestApp extends MonzoApp {
  private TestApplicationComponent testComponent;

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(LocaleHelper.createOverrideContext(base, Locale.UK));
    LocaleHelper.setDefaultLocale(Locale.UK);
  }

  @Override protected AndroidInjector<MonzoApp> applicationInjector() {
    final TestApplicationComponent.Builder builder = DaggerTestApplicationComponent.builder();
    builder.seedInstance(this);
    return (testComponent = builder.build());
  }

  public static TestApplicationComponent getTestComponent(Context context) {
    return ((FunctionalTestApp) context.getApplicationContext()).testComponent;
  }
}
