package glass.phil.monzo.core;

public interface Factory<T, R> {
  R create(T t);
}
