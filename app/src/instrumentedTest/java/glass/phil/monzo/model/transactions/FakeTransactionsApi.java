package glass.phil.monzo.model.transactions;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData;
import io.reactivex.Single;

final class FakeTransactionsApi implements TransactionsApi {
  private final List<TransactionData> serializedTransactions = new ArrayList<>();

  private Throwable error;

  @Override public Single<TransactionsResponse> transactions(String accountId, int limit, @Nullable String since) {
    if (error != null) {
      return Single.error(error);
    }
    final List<TransactionData> transactions;
    if (since == null) {
      transactions = serializedTransactions.subList(0, Math.min(limit, serializedTransactions.size()));
    } else {
      final int start = indexAfter(since);
      if (start < 0) {
        transactions = Collections.emptyList();
      } else {
        final int size = serializedTransactions.size();
        transactions = serializedTransactions.subList(Math.min(start, size - 1), Math.min(start + limit, size));
      }
    }
    return Single.just(new AutoValue_TransactionsApi_TransactionsResponse(transactions));
  }

  private int indexAfter(String transactionId) {
    for (int index = 0; index < serializedTransactions.size(); index++) {
      if (serializedTransactions.get(index).id().equals(transactionId)) {
        return index + 1;
      }
    }
    return -1;
  }

  @SuppressWarnings("Convert2streamapi")
  void setTransactions(List<TestTransaction> transactions) {
    serializedTransactions.clear();
    for (TestTransaction transaction : transactions) {
      serializedTransactions.add(TestTransactionMapper.toTransactionData(transaction));
    }
    Collections.sort(serializedTransactions, (first, second) -> first.created().compareTo(second.created()));
  }

  void setError(Throwable error) {
    this.error = error;
  }
}
