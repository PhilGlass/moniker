package glass.phil.monzo;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

final class LocaleHelper {
  static Context createOverrideContext(Context base, Locale locale) {
    final Configuration overrideConfig = new Configuration(base.getResources().getConfiguration());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      overrideConfig.setLocales(new LocaleList(locale));
    } else {
      overrideConfig.setLocale(locale);
    }
    return base.createConfigurationContext(overrideConfig);
  }

  static void setDefaultLocale(Locale locale) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      LocaleList.setDefault(new LocaleList(locale));
    } else {
      Locale.setDefault(locale);
    }
  }

  private LocaleHelper() {
    throw new AssertionError("No instances");
  }
}
