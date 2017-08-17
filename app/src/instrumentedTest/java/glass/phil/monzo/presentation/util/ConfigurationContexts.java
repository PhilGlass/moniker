package glass.phil.monzo.presentation.util;

import android.content.Context;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;

import java.util.Locale;

final class ConfigurationContexts {
  static Context uk() {
    final Context targetContext = InstrumentationRegistry.getTargetContext();
    final Configuration newConfiguration = new Configuration(targetContext.getResources().getConfiguration());
    newConfiguration.setLocale(Locale.UK);
    return targetContext.createConfigurationContext(newConfiguration);
  }

  private ConfigurationContexts() {
    throw new AssertionError("No instances");
  }
}
