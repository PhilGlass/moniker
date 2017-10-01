package glass.phil.monzo.model.account;

import javax.inject.Inject;

import glass.phil.monzo.core.Collections;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.account.AccountApi.AccountsResponse.Account;
import glass.phil.monzo.model.account.AccountApi.AccountsResponse.Account.AccountType;
import io.reactivex.Single;

final class AccountManagerImpl implements AccountManager {
  private final AccountApi api;
  private final Store<String> store;

  @Inject AccountManagerImpl(AccountApi api, Store<String> store) {
    this.api = api;
    this.store = store;
  }

  @Override public Single<String> accountId() {
    return Single.defer(() -> {
      final String stored = store.get();
      if (stored != null) {
        return Single.just(stored);
      }
      return api.accounts()
          .map(it -> Collections.first(it.accounts(), account -> account.type() == AccountType.PREPAID))
          .map(Account::id)
          .doOnSuccess(store::set);
    });
  }
}
