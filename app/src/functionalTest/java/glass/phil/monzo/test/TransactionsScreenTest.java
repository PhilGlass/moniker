package glass.phil.monzo.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import glass.phil.monzo.FunctionalTestApp;
import glass.phil.monzo.TestData;
import glass.phil.monzo.model.transactions.TestTransaction;
import glass.phil.monzo.model.transactions.TestTransactions;
import glass.phil.monzo.model.transactions.Transaction.DeclineReason;
import glass.phil.monzo.model.transactions.TransactionGenerator;
import glass.phil.monzo.test.screens.DetailsScreen;
import glass.phil.monzo.test.screens.TransactionsScreen;
import glass.phil.monzo.test.server.MonzoServer;
import glass.phil.monzo.test.server.ResetServerRule;
import glass.phil.monzo.test.util.ClearDbRule;
import glass.phil.monzo.test.util.MainActivityRule;
import glass.phil.monzo.test.util.MoreViewMatchers.TextColor;
import glass.phil.monzo.test.util.TestHooks;

import static glass.phil.monzo.model.transactions.TestTransactions.ASDA;
import static glass.phil.monzo.model.transactions.TestTransactions.STARBUCKS;
import static glass.phil.monzo.model.transactions.TestTransactions.atMerchant;

@RunWith(AndroidJUnit4.class)
public class TransactionsScreenTest {
  @Rule public final MainActivityRule activityRule = new MainActivityRule();
  @Rule public final ResetServerRule serverRule = new ResetServerRule();
  @Rule public final ClearDbRule dbRule = new ClearDbRule();

  @Inject TestHooks testHooks;

  @Before public void setUp() {
    FunctionalTestApp.getComponent(InstrumentationRegistry.getTargetContext()).inject(this);
    testHooks.setLoggedIn();
  }

  @Test public void balance() {
    MonzoServer.getInstance().onBalanceRequestReturn(TestData.balance(100_00, 50_00));
    activityRule.launchActivity();

    TransactionsScreen.checkShowingBalance("£100.00", "£50.00");

    MonzoServer.getInstance().onBalanceRequestReturn(TestData.balance(50_00, 22_22));
    testHooks.triggerRefresh();

    TransactionsScreen.checkShowingBalance("£50.00", "£22.22");
  }

  @Test public void noTransactions() {
    openTransactionsScreen(Collections.emptyList());

    TransactionsScreen.checkShowingEmpty();
  }

  @Test public void oneTransaction() {
    openTransactionsScreen(TestTransactions.topUp());

    TransactionsScreen.checkShowingTransactions(1);
  }

  @Test public void manyTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(50);
    replace(transactions, 0, TestTransactions.topUp().withAmount(+50_00));
    replace(transactions, transactions.size() - 1, atMerchant(STARBUCKS).withAmount(-5_55));
    openTransactionsScreen(transactions);

    TransactionsScreen.checkShowingTransactions(transactions.size());

    // The last item in the transactions response is the most recent, so it should be at the top of the list
    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Starbucks")
        .checkShowingAmount("5.55")
        .checkAmountHasColor(TextColor.BLACK)
        .checkLocalAmountHidden()
        .checkNoteHidden();

    TransactionsScreen.onTransactionAtPosition(transactions.size() - 1)
        .checkShowingTitle("Top up")
        .checkShowingAmount("+50.00")
        .checkAmountHasColor(TextColor.GREEN)
        .checkLocalAmountHidden()
        .checkNoteHidden();
  }

  @Test public void zeroToOneTransactions() {
    openTransactionsScreen(Collections.emptyList());

    TransactionsScreen.checkShowingEmpty();

    MonzoServer.getInstance().onTransactionsRequestReturn(TestTransactions.topUp());
    testHooks.triggerRefresh();

    TransactionsScreen.checkShowingTransactions(1);
  }

  @Test public void oneToManyTransactions() {
    final List<TestTransaction> transactions = TransactionGenerator.generateTransactions(50);
    openTransactionsScreen(transactions.get(transactions.size() - 1));

    TransactionsScreen.checkShowingTransactions(1);

    MonzoServer.getInstance().onTransactionsRequestReturn(transactions);
    testHooks.triggerRefresh();

    TransactionsScreen.checkShowingTransactions(transactions.size());
  }

  @Test public void topUp() {
    openTransactionsScreen(TestTransactions.topUp().withAmount(+200_00));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Top up")
        .checkShowingAmount("+200.00")
        .checkAmountHasColor(TextColor.GREEN)
        .checkLocalAmountHidden()
        .checkNoteHidden();
  }

  @Test public void gbp() {
    openTransactionsScreen(atMerchant(ASDA).withAmount(-50_00));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Asda")
        .checkShowingAmount("50.00")
        .checkAmountHasColor(TextColor.BLACK)
        .checkLocalAmountHidden()
        .checkNoteHidden();
  }

  @Test public void foreignCurrency() {
    final Money localAmount = Money.ofMinor(CurrencyUnit.USD, -32_57);
    openTransactionsScreen(atMerchant(STARBUCKS).withAmount(-25_00).withLocalAmount(localAmount));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Starbucks")
        .checkShowingAmount("25.00")
        .checkAmountHasColor(TextColor.BLACK)
        .checkShowingLocalAmount("US$32.57")
        .checkNoteHidden();
  }

  @Test public void refund() {
    openTransactionsScreen(atMerchant(ASDA).withAmount(+55_55));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Asda")
        .checkShowingAmount("+55.55")
        .checkAmountHasColor(TextColor.GREEN)
        .checkLocalAmountHidden()
        .checkNoteHidden();
  }

  @Test public void declined() {
    openTransactionsScreen(atMerchant(ASDA).withDeclineReason(DeclineReason.CARD_BLOCKED));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Asda")
        .checkShowingDeclined()
        .checkLocalAmountHidden()
        .checkNoteHidden();
  }

  @Test public void hideAmount() {
    openTransactionsScreen(atMerchant(ASDA).withHideAmount(true));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Asda")
        .checkAmountHidden()
        .checkLocalAmountHidden()
        .checkNoteHidden();
  }

  @Test public void notes() {
    openTransactionsScreen(atMerchant(ASDA).withAmount(-10_00).withNotes("This is a note"));

    TransactionsScreen.onFirstTransaction()
        .checkShowingTitle("Asda")
        .checkShowingAmount("10.00")
        .checkAmountHasColor(TextColor.BLACK)
        .checkLocalAmountHidden()
        .checkShowingNote("This is a note");
  }

  @Test public void clickingTransaction_navigatesToDetailsScreen() {
    openTransactionsScreen(TestTransactions.topUp());

    TransactionsScreen.checkDisplayed();
    TransactionsScreen.clickFirstTransaction();

    DetailsScreen.checkDisplayed();
  }

  private void openTransactionsScreen(TestTransaction transaction) {
    openTransactionsScreen(Collections.singletonList(transaction));
  }

  private void openTransactionsScreen(List<TestTransaction> transactions) {
    MonzoServer.getInstance().onTransactionsRequestReturn(transactions);
    activityRule.launchActivity();
  }

  private static void replace(List<TestTransaction> transactions, int position, TestTransaction replacement) {
    transactions.set(position, replacement.withCreated(transactions.get(position).created()));
  }
}
