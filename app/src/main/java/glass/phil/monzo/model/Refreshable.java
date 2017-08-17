package glass.phil.monzo.model;

import io.reactivex.Completable;

public interface Refreshable {
  Completable refresh();
}
