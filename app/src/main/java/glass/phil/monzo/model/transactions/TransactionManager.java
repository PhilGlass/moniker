package glass.phil.monzo.model.transactions;

import java.util.List;

import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.model.Refreshable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface TransactionManager extends Refreshable {
  Observable<List<Transaction>> transactions();

  Single<Optional<TransactionHistory>> topUpHistory();
  Single<Optional<TransactionHistory>> merchantHistory(String merchantId);
}
