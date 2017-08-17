package glass.phil.monzo.model.account;

import javax.inject.Inject;

import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.account.AccountApi.AccountsResponse.Account;
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
          .map(response -> response.accounts().get(0))
          .map(Account::id)
          .doOnSuccess(store::set);
    });
  }
}
