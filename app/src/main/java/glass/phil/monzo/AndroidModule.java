package glass.phil.monzo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module abstract class AndroidModule {
  @Binds abstract Context context(Application context);

  @Provides @Reusable static SharedPreferences sharedPreferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }
}
