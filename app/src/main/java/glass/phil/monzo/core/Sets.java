package glass.phil.monzo.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Sets {
  @SafeVarargs public static <T> Set<T> newHashSet(T... items) {
    final HashSet<T> set = new HashSet<>(items.length);
    Collections.addAll(set, items);
    return set;
  }

  private Sets() {
    throw new AssertionError("No instances");
  }
}
