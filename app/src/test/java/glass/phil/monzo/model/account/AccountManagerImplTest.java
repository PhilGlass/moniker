package glass.phil.monzo.model.account;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.Collections;

import glass.phil.monzo.model.InMemoryStore;
import glass.phil.monzo.model.Store;
import glass.phil.monzo.model.account.AccountApi.AccountsResponse;
import io.reactivex.Single;

import static glass.phil.monzo.model.StoreAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class AccountManagerImplTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private final AccountApi api = mock(AccountApi.class);
  private final Store<String> store = new InMemoryStore<>();
  private final AccountManagerImpl accountManager = new AccountManagerImpl(api, store);

  @Test public void fromStore() {
    store.set("account_1");

    accountManager.accountId().test().assertValue("account_1");
    verify(api, never()).accounts();
  }

  @Test public void fromApi() {
    when(api.accounts()).thenReturn(Single.just(accountsResponse("account_1")));

    accountManager.accountId().test().assertValue("account_1");
    assertThat(store).hasStoredValue("account_1");
  }

  @Test public void errorFetchingAccountsResponse() {
    when(api.accounts()).thenReturn(Single.error(new IOException()));

    accountManager.accountId().test().assertError(IOException.class);
    assertThat(store).hasNoStoredValue();
  }

  private static AccountsResponse accountsResponse(String accountId) {
    final AccountsResponse.Account account = new AutoValue_AccountApi_AccountsResponse_Account(accountId);
    return new AutoValue_AccountApi_AccountsResponse(Collections.singletonList(account));
  }
}
