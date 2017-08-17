package glass.phil.monzo.core;

public interface Command<T> {
  void execute(T t);
}
