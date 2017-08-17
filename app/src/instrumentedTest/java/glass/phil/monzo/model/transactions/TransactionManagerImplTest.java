package glass.phil.monzo.model.transactions;

import android.support.test.InstrumentationRegistry;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import glass.phil.monzo.core.rx.Optional;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static glass.phil.monzo.TestData.history;
import static glass.phil.monzo.model.transactions.TestTransactions.ASDA;
import static glass.phil.monzo.model.transactions.TestTransactions.STARBUCKS;
import static glass.phil.monzo.model.transactions.TestTransactions.atMerchant;
import static glass.phil.monzo.model.transactions.TestTransactions.topUp;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public final class TransactionManagerImplTest {
  private final FakeTransactionsApi api = new FakeTransactionsApi();
  private final DatabaseTestHelper db;

  private final TransactionManagerImpl transactionManager;

  public TransactionManagerImplTest() {
    final TransactionsDb transactionsDb = new TransactionsDb(InstrumentationRegistry.getTargetContext(), null);
    final SqlBrite sqlBrite = new SqlBrite.Builder().build();
    final BriteDatabase briteDb = sqlBrite.wrapDatabaseHelper(transactionsDb, Schedulers.trampoline());
    db = new DatabaseTestHelper(briteDb);
    transactionManager = new TransactionManagerImpl(() -> Single.just("account_1"), api, briteDb);
  }

  @Test public void initialLoad_withNoResults() {
    api.setTransactions(emptyList());

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(emptyList());
  }

  @Test public void initialLoad_withLessThanOnePageOfTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(10);
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions);
  }

  @Test public void initialLoad_withExactlyOnePageOfTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(100);
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions);
  }

  @Test public void initialLoad_withMultiplePagesOfTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(250);
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions);
  }

  @Test public void initialLoadFailed() {
    api.setError(new IOException());

    transactionManager.refresh().test().assertError(IOException.class);
    db.assertContainsExactly(emptyList());
  }

  @Test public void incrementalUpdate_withNoNewTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(10);
    db.insertTransactions(transactions);
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions);
  }

  @Test public void incrementalUpdate_withLessThanOnePageOfNewTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(10);
    db.insertTransactions(transactions.get(5));
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions.subList(5, 10));
  }

  @Test public void incrementalUpdate_withExactlyOnePageOfNewTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(120);
    db.insertTransactions(transactions.get(20));
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions.subList(20, 120));
  }

  @Test public void incrementalUpdate_withMultiplePagesOfNewTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(250);
    db.insertTransactions(transactions.get(50));
    api.setTransactions(transactions);

    transactionManager.refresh().test().assertComplete();
    db.assertContainsExactly(transactions.subList(50, 250));
  }

  @Test public void incrementalUpdateFailed() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(10);
    db.insertTransactions(transactions.subList(0, 5));
    api.setError(new IOException());

    transactionManager.refresh().test().assertError(IOException.class);
    db.assertContainsExactly(transactions.subList(0, 5));
  }

  @Test public void transactions_initiallyEmptyDatabase() {
    transactionManager.transactions().test().assertValue(emptyList());
  }

  @Test public void transactions_initiallyPopulatedDatabase() {
    final TestTransaction transaction = atMerchant(ASDA);
    db.insertTransactions(transaction);

    transactionManager.transactions().test().assertValue(singletonList(transaction.toTransaction()));
  }

  @SuppressWarnings("unchecked")
  @Test public void transactions_databaseUpdated() {
    final TestObserver<List<Transaction>> transactionsObserver = transactionManager.transactions().test();
    transactionsObserver.assertValue(emptyList());

    final TestTransaction transaction = atMerchant(ASDA);
    db.insertTransactions(transaction);
    transactionsObserver.assertValues(emptyList(), singletonList(transaction.toTransaction()));
  }

  @Test public void topUpHistory_noTopUps() {
    transactionManager.topUpHistory().test().assertValue(Optional.none());
  }

  @Test public void topUpHistory_oneTopUp() {
    db.insertTransactions(
        topUp().withAmount(+50_00)
    );

    transactionManager.topUpHistory().test().assertValue(history(1, +50_00, +50_00));
  }

  @Test public void topUpHistory_multipleTopUps() {
    db.insertTransactions(
        topUp().withAmount(+50_00),
        topUp().withAmount(+75_00),
        topUp().withAmount(+100_00)
    );

    transactionManager.topUpHistory().test().assertValue(history(3, +75_00, +225_00));
  }

  @Test public void topUpHistory_topUpsAndTransactions() {
    db.insertTransactions(
        topUp().withAmount(+50_00),
        atMerchant(ASDA).withAmount(-75_00),
        topUp().withAmount(+100_00),
        atMerchant(ASDA).withAmount(-125_00),
        topUp().withAmount(+150_00)
    );

    transactionManager.topUpHistory().test().assertValue(history(3, +100_00, +300_00));
  }

  @Test public void merchantHistory_noTransactions() {
    transactionManager.merchantHistory(ASDA.id()).test().assertValue(Optional.none());
  }

  @Test public void merchantHistory_oneTransaction() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-100_00)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(history(1, -100_00, -100_00));
  }

  @Test public void merchantHistory_multipleTransactions() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-100_00),
        atMerchant(ASDA).withAmount(-101_00),
        atMerchant(ASDA).withAmount(-102_00)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(history(3, -101_00, -303_00));
  }

  @Test public void merchantHistory_refundedTransaction() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-50_00),
        atMerchant(ASDA).withAmount(-70_00),
        atMerchant(ASDA).withAmount(+50_00)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(history(2, -60_00, -120_00));
  }

  @Test public void merchantHistory_transactionsWithDifferentMerchants() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-36_00),
        atMerchant(STARBUCKS).withAmount(-5_00),
        atMerchant(ASDA).withAmount(-48_00),
        atMerchant(STARBUCKS).withAmount(-3_00),
        atMerchant(ASDA).withAmount(-24_00)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(history(3, -36_00, -108_00));
    transactionManager.merchantHistory(STARBUCKS.id()).test().assertValue(history(2, -4_00, -8_00));
  }

  @Test public void merchantHistory_transactionsAndTopUps() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-100_00),
        topUp().withAmount(+110_00),
        atMerchant(ASDA).withAmount(-120_00),
        topUp().withAmount(+130_00),
        atMerchant(ASDA).withAmount(-140_00)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(history(3, -120_00, -360_00));
  }

  @Test public void merchantHistory_oneExcludedTransaction() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-50_00).withIncludeInSpending(false)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(Optional.none());
  }

  @Test public void merchantHistory_includedAndExcludedTransactions() {
    db.insertTransactions(
        atMerchant(ASDA).withAmount(-50_00),
        atMerchant(ASDA).withAmount(-70_00),
        atMerchant(ASDA).withAmount(-90_00).withIncludeInSpending(false)
    );

    transactionManager.merchantHistory(ASDA.id()).test().assertValue(history(2, -60_00, -120_00));
  }
}
