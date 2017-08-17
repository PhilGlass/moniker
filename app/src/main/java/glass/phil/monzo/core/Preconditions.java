package glass.phil.monzo.core;

public final class Preconditions {
  public static <T> T checkNotNull(T arg) {
    if (arg == null) {
      throw new NullPointerException();
    }
    return arg;
  }

  private Preconditions() {
    throw new AssertionError("No instances");
  }
}
