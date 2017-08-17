package glass.phil.monzo.model.balance;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.inject.Inject;

import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.account.AccountManager;
import io.reactivex.Completable;
import io.reactivex.Observable;

final class BalanceManagerImpl implements BalanceManager {
  private final AccountManager accountManager;
  private final BalanceApi api;
  private final Store<Balance> balanceStore;

  @Inject BalanceManagerImpl(AccountManager accountManager, BalanceApi api, Store<Balance> balanceStore) {
    this.accountManager = accountManager;
    this.api = api;
    this.balanceStore = balanceStore;
  }

  @Override public Completable refresh() {
    return accountManager.accountId()
        .flatMap(api::balance)
        .map(this::fromResponse)
        .doOnSuccess(balanceStore::set)
        .toCompletable();
  }

  private Balance fromResponse(BalanceApi.BalanceResponse response) {
    final CurrencyUnit currency = CurrencyUnit.of(response.currency());
    final Money balance = Money.ofMinor(currency, response.balance());
    final Money spentToday = Money.ofMinor(currency, response.spend_today()).abs();
    return Balance.create(balance, spentToday);
  }

  @Override public Observable<Balance> balance() {
    return balanceStore.asObservable()
        .filter(Optional::isSome)
        .map(Optional::value);
  }
}
