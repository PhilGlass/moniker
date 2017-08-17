package glass.phil.monzo.model;

import android.support.annotation.Nullable;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;

public final class StoreAssertions<T> extends AbstractAssert<StoreAssertions<T>, Store<T>> {
  private StoreAssertions(Store<T> actual) {
    super(actual, StoreAssertions.class);
  }

  public static <T> StoreAssertions<T> assertThat(Store<T> actual) {
    return new StoreAssertions<>(actual);
  }

  public StoreAssertions<T> hasStoredValue(@Nullable T value) {
    isNotNull();

    final T storedValue = actual.get();
    if (!Objects.areEqual(storedValue, value)) {
      failWithMessage("Expected stored value to be <%s> but was <%s>", value, storedValue);
    }

    return this;
  }

  public StoreAssertions<T> hasNoStoredValue() {
    return hasStoredValue(null);
  }
}
