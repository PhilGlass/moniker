package glass.phil.monzo.core.rx;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/** Used to represent optional values in RxJava 2.x, which does not allow null values to pass through the stream. */
@AutoValue public abstract class Optional<T> {
  private static final Optional<?> NONE = create(null);

  @SuppressWarnings("unchecked")
  public static <T> Optional<T> none() {
    return (Optional<T>) NONE;
  }

  public static <T> Optional<T> some(T value) {
    return create(value);
  }

  public static <T> Optional<T> of(@Nullable T value) {
    return value == null ? none() : some(value);
  }

  private static <T> Optional<T> create(@Nullable T value) {
    return new AutoValue_Optional<>(value);
  }

  @Nullable public abstract T value();

  public boolean isSome() {
    return value() != null;
  }
}
