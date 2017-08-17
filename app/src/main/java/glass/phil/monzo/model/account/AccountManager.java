package glass.phil.monzo.model.account;

import io.reactivex.Single;

public interface AccountManager {
  Single<String> accountId();
}
