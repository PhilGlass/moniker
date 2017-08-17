package glass.phil.monzo.model;

import android.support.annotation.Nullable;

import com.jakewharton.rxrelay2.BehaviorRelay;

import glass.phil.monzo.core.rx.Optional;
import io.reactivex.Observable;

public final class InMemoryStore<T> implements Store<T> {
  private final BehaviorRelay<Optional<T>> relay = BehaviorRelay.createDefault(Optional.none());

  @Nullable @Override public T get() {
    return relay.getValue().value();
  }

  @Override public void set(@Nullable T value) {
    relay.accept(Optional.of(value));
  }

  @Override public Observable<Optional<T>> asObservable() {
    return relay;
  }
}
