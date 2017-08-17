package glass.phil.monzo.model.balance;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.io.IOException;

import glass.phil.monzo.model.InMemoryStore;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.account.AccountManager;
import glass.phil.monzo.model.balance.BalanceApi.BalanceResponse;
import glass.phil.monzo.model.balance.BalanceManager.Balance;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static glass.phil.monzo.TestData.balance;
import static glass.phil.monzo.model.StoreAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class BalanceManagerImplTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final AccountManager accountManager = () -> Single.just("account_1");
  private final BalanceApi api = mock(BalanceApi.class);
  private final Store<Balance> store = new InMemoryStore<>();
  private final BalanceManagerImpl balanceManager = new BalanceManagerImpl(accountManager, api, store);

  @Test public void failedRefresh() {
    when(api.balance("account_1")).thenReturn(Single.error(new IOException()));

    balanceManager.refresh().test().assertError(IOException.class);
    assertThat(store).hasNoStoredValue();
  }

  @Test public void successfulRefresh() {
    when(api.balance("account_1")).thenReturn(Single.just(balanceResponse(80_00, 40_00)));

    balanceManager.refresh().test().assertComplete();
    assertThat(store).hasStoredValue(balance(80_00, 40_00));
  }

  @Test public void twoSuccessfulRefreshes() {
    when(api.balance("account_1"))
        .thenReturn(Single.just(balanceResponse(80_00, 40_00)))
        .thenReturn(Single.just(balanceResponse(40_00, 20_00)));

    balanceManager.refresh().test().assertComplete();
    assertThat(store).hasStoredValue(balance(80_00, 40_00));

    balanceManager.refresh().test().assertComplete();
    assertThat(store).hasStoredValue(balance(40_00, 20_00));
  }

  @Test public void successfulAndFailedRefreshes() {
    when(api.balance("account_1"))
        .thenReturn(Single.just(balanceResponse(80_00, 40_00)))
        .thenReturn(Single.error(new IOException()));

    balanceManager.refresh().test().assertComplete();
    assertThat(store).hasStoredValue(balance(80_00, 40_00));

    balanceManager.refresh().test().assertError(IOException.class);
    assertThat(store).hasStoredValue(balance(80_00, 40_00));
  }

  @Test public void noStoredBalance() {
    balanceManager.balance().test().assertNoValues();
  }

  @Test public void storedBalance() {
    store.set(balance(100_00, 20_00));

    balanceManager.balance().test().assertValue(balance(100_00, 20_00));
  }

  @Test public void balanceChange() {
    store.set(balance(100_00, 20_00));

    final TestObserver<Balance> balanceObserver = balanceManager.balance().test();
    balanceObserver.assertValue(balance(100_00, 20_00));

    store.set(balance(80_00, 40_00));
    balanceObserver.assertValues(balance(100_00, 20_00), balance(80_00, 40_00));
  }

  private static BalanceResponse balanceResponse(long balancePennies, long spentTodayPennies) {
    return new AutoValue_BalanceApi_BalanceResponse("GBP", balancePennies, spentTodayPennies);
  }
}
