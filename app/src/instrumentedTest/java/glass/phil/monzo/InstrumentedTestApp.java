package glass.phil.monzo;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public final class InstrumentedTestApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }
}
