package glass.phil.monzo.model;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;

import java.io.IOException;

import glass.phil.monzo.core.rx.Optional;
import io.reactivex.Observable;

final class MoshiPreferenceStore<T> implements Store<T> {
  private final JsonAdapter<T> adapter;
  private final SharedPreferences prefs;
  private final String key;

  MoshiPreferenceStore(JsonAdapter<T> adapter, SharedPreferences prefs, String key) {
    this.adapter = adapter;
    this.prefs = prefs;
    this.key = key;
  }

  @Nullable @Override public T get() {
    final String serialized = prefs.getString(key, null);
    try {
      return serialized == null ? null : adapter.fromJson(serialized);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override public void set(@Nullable T value) {
    prefs.edit().putString(key, value == null ? null : adapter.toJson(value)).apply();
  }

  @Override public Observable<Optional<T>> asObservable() {
    return keyChanges().filter(key::equals)
        .startWith(key)
        .map(it -> Optional.of(get()))
        .distinctUntilChanged();
  }

  private Observable<String> keyChanges() {
    return Observable.create(onSubscribe -> {
      final OnSharedPreferenceChangeListener listener = (prefs, key) -> onSubscribe.onNext(key);
      onSubscribe.setCancellable(() -> prefs.unregisterOnSharedPreferenceChangeListener(listener));
      prefs.registerOnSharedPreferenceChangeListener(listener);
    });
  }
}
