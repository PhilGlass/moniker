package glass.phil.monzo.model.transactions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite2.BriteDatabase;

import org.joda.money.Money;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import glass.phil.monzo.model.transactions.Transaction.DeclineReason;
import glass.phil.monzo.model.transactions.Transaction.Merchant;
import glass.phil.monzo.model.transactions.Transaction.Merchant.Address;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData.MerchantData;

import static glass.phil.monzo.core.Cursors.getBoolean;
import static glass.phil.monzo.core.Cursors.getDouble;
import static glass.phil.monzo.core.Cursors.getLong;
import static glass.phil.monzo.core.Cursors.getString;
import static org.assertj.core.api.Assertions.assertThat;

final class DatabaseTestHelper {
  private final BriteDatabase db;

  DatabaseTestHelper(BriteDatabase db) {
    this.db = db;
  }

  void insertTransactions(TestTransaction... transactions) {
    insertTransactions(Arrays.asList(transactions));
  }

  void insertTransactions(List<TestTransaction> transactions) {
    try (BriteDatabase.Transaction dbTransaction = db.newTransaction()) {
      for (TestTransaction transaction : transactions) {
        final TransactionData transactionData = transaction.toTransactionData();
        final MerchantData merchantData = transactionData.merchant();
        if (merchantData != null) {
          db.insert("merchants", TransactionMapper.toContentValues(merchantData), SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.insert("transactions", TransactionMapper.toContentValues(transactionData));
      }
      dbTransaction.markSuccessful();
    }
  }

  void assertContainsExactly(List<TestTransaction> transactions) {
    final Map<String, TestTransaction> transactionMap = collectTransactions(transactions);
    try (Cursor cursor = db.query("SELECT * FROM transactions")) {
      while (cursor.moveToNext()) {
        final String id = getString(cursor, "id");
        final TestTransaction transaction = transactionMap.remove(id);
        assertThat(transaction).isNotNull();
        assertThat(transaction.category().name().equalsIgnoreCase(getString(cursor, "category")));
        assertThat(transaction.created().toEpochMilli()).isEqualTo(getLong(cursor, "created"));
        final Money amount = transaction.amount();
        assertThat(amount.getCurrencyUnit().getCurrencyCode()).isEqualTo(getString(cursor, "currency"));
        assertThat(amount.getAmountMinorLong()).isEqualTo(getLong(cursor, "amount"));
        final Money localAmount = transaction.localAmount();
        assertThat(localAmount.getCurrencyUnit().getCurrencyCode()).isEqualTo(getString(cursor, "local_currency"));
        assertThat(localAmount.getAmountMinorLong()).isEqualTo(getLong(cursor, "local_amount"));
        final DeclineReason declineReason = transaction.declineReason();
        if (declineReason == null) {
          assertThat(getString(cursor, "decline_reason")).isNull();
        } else {
          assertThat(declineReason.name().equalsIgnoreCase(getString(cursor, "decline_reason")));
        }
        assertThat(transaction.includeInSpending()).isEqualTo(getBoolean(cursor, "include_in_spending"));
        assertThat(transaction.topUp()).isEqualTo(getBoolean(cursor, "top_up"));
        assertThat(transaction.hideAmount()).isEqualTo(getBoolean(cursor, "hide_amount"));
        assertThat(transaction.notes()).isEqualTo(getString(cursor, "notes"));
        final Merchant merchant = transaction.merchant();
        assertThat(merchant == null ? null : merchant.id()).isEqualTo(getString(cursor, "merchant"));
      }
    }
    assertThat(transactionMap).isEmpty();

    final Map<String, Merchant> merchantMap = collectMerchants(transactions);
    try (Cursor cursor = db.query("SELECT * FROM merchants")) {
      while (cursor.moveToNext()) {
        final String id = getString(cursor, "id");
        final Merchant merchant = merchantMap.remove(id);
        assertThat(merchant).isNotNull();
        assertThat(merchant.name()).isEqualTo(getString(cursor, "name"));
        assertThat(merchant.online()).isEqualTo(getBoolean(cursor, "online"));
        assertThat(merchant.logoUrl()).isEqualTo(getString(cursor, "logo_url"));
        final Address address = merchant.address();
        assertThat(address.formattedAddress()).isEqualTo(getString(cursor, "address"));
        assertThat(address.latitude()).isEqualTo(getDouble(cursor, "latitude"));
        assertThat(address.longitude()).isEqualTo(getDouble(cursor, "longitude"));
      }
    }
    assertThat(merchantMap).isEmpty();
  }

  private static Map<String, TestTransaction> collectTransactions(List<TestTransaction> transactions) {
    final Map<String, TestTransaction> transactionMap = new HashMap<>(transactions.size());
    for (TestTransaction transaction : transactions) {
      transactionMap.put(transaction.id(), transaction);
    }
    return transactionMap;
  }

  private static Map<String, Merchant> collectMerchants(List<TestTransaction> transactions) {
    final Map<String, Merchant> merchantMap = new HashMap<>();
    for (TestTransaction transaction : transactions) {
      final Merchant merchant = transaction.merchant();
      if (merchant != null) {
        merchantMap.put(merchant.id(), merchant);
      }
    }
    return merchantMap;
  }
}
