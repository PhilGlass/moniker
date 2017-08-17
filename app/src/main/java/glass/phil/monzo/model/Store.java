package glass.phil.monzo.model;

import android.support.annotation.Nullable;

import java.lang.reflect.Type;

import glass.phil.monzo.core.rx.Optional;
import io.reactivex.Observable;

public interface Store<T> {
  @Nullable T get();
  void set(@Nullable T value);
  Observable<Optional<T>> asObservable();

  interface Factory {
    <T> Store<T> create(Type type, String key);
  }
}
