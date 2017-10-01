package glass.phil.monzo.core;

public interface Predicate<T> {
  boolean matches(T t);
}
