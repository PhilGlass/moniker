package glass.phil.monzo;

import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public final class FunctionalTestApp extends DaggerApplication {
  private TestApplicationComponent component;

  @Override public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }

  @Override protected AndroidInjector<FunctionalTestApp> applicationInjector() {
    final TestApplicationComponent.Builder builder = DaggerTestApplicationComponent.builder();
    builder.seedInstance(this);
    return (component = builder.build());
  }

  public static TestApplicationComponent getComponent(Context context) {
    return ((FunctionalTestApp) context.getApplicationContext()).component;
  }
}
