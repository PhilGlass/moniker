package glass.phil.monzo.model;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.CLASS;

public final class Clocks {
  @Qualifier @Retention(CLASS) public @interface Utc {}

  @Qualifier @Retention(CLASS) public @interface Local {}

  private Clocks() {
    throw new AssertionError("No instances");
  }
}
