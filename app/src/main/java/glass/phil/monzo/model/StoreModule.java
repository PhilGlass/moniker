package glass.phil.monzo.model;

import android.content.SharedPreferences;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module public abstract class StoreModule {
  @Provides @Reusable static Store.Factory storeFactory(Moshi moshi, SharedPreferences preferences) {
    return new Store.Factory() {
      @Override public <T> Store<T> create(Type type, String key) {
        final JsonAdapter<T> jsonAdapter = moshi.adapter(type);
        return new MoshiPreferenceStore<>(jsonAdapter, preferences, key);
      }
    };
  }
}
