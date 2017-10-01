package glass.phil.monzo.core;

import java.util.Collection;
import java.util.NoSuchElementException;

public final class Collections {
  public static <T> T first(Collection<? extends T> collection, Predicate<? super T> predicate) {
    for (T item : collection) {
      if (predicate.matches(item)) {
        return item;
      }
    }
    throw new NoSuchElementException("No element matches the predicate");
  }

  private Collections() {
    throw new AssertionError("No instances");
  }
}
