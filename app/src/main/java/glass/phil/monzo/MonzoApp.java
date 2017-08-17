package glass.phil.monzo;

import com.jakewharton.threetenabp.AndroidThreeTen;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public final class MonzoApp extends DaggerApplication {
  @Override public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }

  @Override protected AndroidInjector<MonzoApp> applicationInjector() {
    return DaggerApplicationComponent.builder().create(this);
  }
}
