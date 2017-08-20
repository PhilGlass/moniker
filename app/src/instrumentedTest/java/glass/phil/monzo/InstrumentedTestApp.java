package glass.phil.monzo;

import android.app.Application;
import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Locale;

public final class InstrumentedTestApp extends Application {
  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(LocaleHelper.createOverrideContext(base, Locale.UK));
    LocaleHelper.setDefaultLocale(Locale.UK);
  }

  @Override public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }
}
