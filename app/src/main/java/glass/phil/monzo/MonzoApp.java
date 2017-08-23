package glass.phil.monzo;

import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import glass.phil.monzo.presentation.transactions.details.GoogleMapHacks;

public class MonzoApp extends DaggerApplication {
  @Inject ApplicationComponent component;

  @Override public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
    GoogleMapHacks.initialiseMaps(this);
  }

  @Override protected AndroidInjector<MonzoApp> applicationInjector() {
    return DaggerApplicationComponent.builder().create(this);
  }

  public static ApplicationComponent getComponent(Context context) {
    return ((MonzoApp) context.getApplicationContext()).component;
  }
}
