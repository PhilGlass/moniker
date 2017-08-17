package glass.phil.monzo.core.rx;

import io.reactivex.Single;
import io.reactivex.functions.BiFunction;

public final class Singles {
  public static <T1, T2, R> Single<R> flatZip(Single<T1> first, Single<T2> second,
      BiFunction<? super T1, ? super T2, ? extends Single<R>> mappingFunction) {
    return Single.zip(first, second, mappingFunction).flatMap(it -> it);
  }

  private Singles() {
    throw new AssertionError("No instances");
  }
}
