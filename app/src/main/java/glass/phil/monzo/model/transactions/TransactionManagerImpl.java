package glass.phil.monzo.model.transactions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite2.BriteDatabase;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import glass.phil.monzo.core.Sets;
import glass.phil.monzo.core.rx.Optional;
import glass.phil.monzo.core.rx.Singles;
import glass.phil.monzo.model.account.AccountManager;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static glass.phil.monzo.model.transactions.TransactionMapper.toContentValues;

final class TransactionManagerImpl implements TransactionManager {
  private static final int LIMIT = 100;
  private static final Set<String> TABLES = Sets.newHashSet("transactions", "merchants");

  private static final String LATEST_TRANSACTION_ID = "" +
      "SELECT id\n" +
      "FROM transactions\n" +
      "ORDER BY created DESC\n" +
      "LIMIT 1";

  private static final String ALL_TRANSACTIONS = "" +
      "SELECT transactions.*, m.name, m.online, m.logo_url, m.address, m.latitude, m.longitude\n" +
      "FROM transactions\n" +
      "LEFT JOIN merchants AS m\n" +
      "ON transactions.merchant = m.id\n" +
      "ORDER BY created DESC";

  private static final String TOP_UP_HISTORY = "" +
      "SELECT currency, count(*), avg(amount), total(amount)\n" +
      "FROM transactions\n" +
      "WHERE top_up = 1\n" +
      "GROUP BY NULL\n" +
      "LIMIT 1";

  private static final String MERCHANT_HISTORY = "" +
      "SELECT currency, count(*), avg(amount), total(amount)\n" +
      "FROM transactions\n" +
      "WHERE merchant = ? AND amount < 0 AND include_in_spending = 1\n" +
      "GROUP BY NULL\n" +
      "LIMIT 1";

  private final AccountManager accountManager;
  private final TransactionsApi api;
  private final BriteDatabase db;

  @Inject TransactionManagerImpl(AccountManager accountManager, TransactionsApi api, BriteDatabase db) {
    this.accountManager = accountManager;
    this.api = api;
    this.db = db;
  }

  @Override public Completable refresh() {
    return Singles.flatZip(accountManager.accountId(), latestTransactionId(),
        (accountId, transactionId) -> fetchTransactions(accountId, transactionId.value()))
        .doOnSuccess(this::writeToDb)
        .toCompletable();
  }

  private Single<Optional<String>> latestTransactionId() {
    return Single.fromCallable(() -> db.query(LATEST_TRANSACTION_ID))
        .map(cursor -> cursor.moveToFirst() ? Optional.some(cursor.getString(0)) : Optional.none());
  }

  private Single<List<TransactionData>> fetchTransactions(String accountId, @Nullable String since) {
    return api.transactions(accountId, LIMIT, since)
        .map(TransactionsResponse::transactions)
        .flatMapObservable(transactions -> {
          if (transactions.size() < LIMIT) {
            return Observable.just(transactions);
          }
          final String lastId = transactions.get(transactions.size() - 1).id();
          return Observable.just(transactions).concatWith(fetchTransactions(accountId, lastId).toObservable());
        }).collectInto(new ArrayList<>(), List::addAll);
  }

  private void writeToDb(List<TransactionData> transactions) {
    try (BriteDatabase.Transaction dbTransaction = db.newTransaction()) {
      for (TransactionData transaction : transactions) {
        final TransactionData.MerchantData merchant = transaction.merchant();
        if (merchant != null) {
          db.insert("merchants", toContentValues(transaction.merchant()), SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.insert("transactions", toContentValues(transaction), SQLiteDatabase.CONFLICT_REPLACE);
      }
      dbTransaction.markSuccessful();
    }
  }

  @Override public Observable<List<Transaction>> transactions() {
    return db.createQuery(TABLES, ALL_TRANSACTIONS).mapToList(TransactionMapper::fromCursor);
  }

  @Override public Single<Optional<TransactionHistory>> topUpHistory() {
    return Single.fromCallable(() -> db.query(TOP_UP_HISTORY)).map(this::fromCursor);
  }

  @Override public Single<Optional<TransactionHistory>> merchantHistory(String merchantId) {
    return Single.fromCallable(() -> db.query(MERCHANT_HISTORY, merchantId)).map(this::fromCursor);
  }

  private Optional<TransactionHistory> fromCursor(Cursor cursor) {
    if (cursor.moveToFirst()) {
      final CurrencyUnit currency = CurrencyUnit.of(cursor.getString(0));
      final long transactions = cursor.getLong(1);
      final Money average = Money.ofMinor(currency, (long) cursor.getFloat(2));
      final Money total = Money.ofMinor(currency, cursor.getLong(3));
      return Optional.of(TransactionHistory.create(transactions, average, total));
    }
    return Optional.none();
  }
}
