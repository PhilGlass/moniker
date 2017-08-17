package glass.phil.monzo.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import javax.inject.Inject;

import glass.phil.monzo.FunctionalTestApp;
import glass.phil.monzo.model.transactions.TestTransaction;
import glass.phil.monzo.model.transactions.TestTransactions;
import glass.phil.monzo.model.transactions.Transaction.DeclineReason;
import glass.phil.monzo.test.screens.DetailsScreen;
import glass.phil.monzo.test.screens.TransactionsScreen;
import glass.phil.monzo.test.server.MonzoServer;
import glass.phil.monzo.test.server.ResetServerRule;
import glass.phil.monzo.test.util.ClearDbRule;
import glass.phil.monzo.test.util.MainActivityRule;
import glass.phil.monzo.test.util.MoreViewMatchers.TextColor;
import glass.phil.monzo.test.util.TestHooks;

import static glass.phil.monzo.model.transactions.TestTransactions.AMAZON;
import static glass.phil.monzo.model.transactions.TestTransactions.ASDA;
import static glass.phil.monzo.model.transactions.TestTransactions.STARBUCKS;
import static glass.phil.monzo.model.transactions.TestTransactions.atMerchant;

@RunWith(AndroidJUnit4.class)
public class DetailsScreenTest {
  @Rule public final MainActivityRule activityRule = new MainActivityRule();
  @Rule public final ResetServerRule serverRule = new ResetServerRule();
  @Rule public final ClearDbRule dbRule = new ClearDbRule();

  @Inject TestHooks testHooks;

  @Before public void setUp() {
    FunctionalTestApp.getComponent(InstrumentationRegistry.getTargetContext()).inject(this);
    testHooks.setLoggedIn();
  }

  @Test public void topUp() {
    openDetailsScreen(TestTransactions.topUp().withAmount(+150_00));

    DetailsScreen.onDetails()
        .checkShowingToolbarWithTitle("Top up")
        .checkMapHidden()
        .checkShowingTitle("Top up")
        .checkShowingAmount("£150.00")
        .checkAmountHasColor(TextColor.GREEN)
        .checkLocalAmountHidden()
        .checkAddressHidden()
        .checkDeclineReasonHidden()
        .checkNotesHidden()
        .checkShowingTopUpHistory("1", "£150.00", "£150.00");
  }

  @Test public void gbp() {
    openDetailsScreen(atMerchant(ASDA).withAmount(-50_00));

    DetailsScreen.onDetails()
        .checkToolbarHidden()
        .checkShowingMap()
        .checkShowingTitle("Asda")
        .checkShowingAmount("£50.00")
        .checkAmountHasColor(TextColor.BLACK)
        .checkLocalAmountHidden()
        .checkShowingAddress()
        .checkDeclineReasonHidden()
        .checkNotesHidden()
        .checkShowingMerchantHistory("1", "£50.00", "£50.00");
  }

  @Test public void foreignCurrency() {
    final Money localAmount = Money.ofMinor(CurrencyUnit.USD, -32_57);
    openDetailsScreen(atMerchant(STARBUCKS).withAmount(-25_00).withLocalAmount(localAmount));

    DetailsScreen.onDetails()
        .checkToolbarHidden()
        .checkShowingMap()
        .checkShowingTitle("Starbucks")
        .checkShowingAmount("£25.00")
        .checkAmountHasColor(TextColor.BLACK)
        .checkShowingLocalAmount("US$32.57")
        .checkShowingAddress()
        .checkDeclineReasonHidden()
        .checkNotesHidden()
        .checkShowingMerchantHistory("1", "£25.00", "£25.00");
  }

  @Test public void refund() {
    openDetailsScreen(atMerchant(ASDA).withAmount(+19_99));

    DetailsScreen.onDetails()
        .checkToolbarHidden()
        .checkShowingMap()
        .checkShowingTitle("Asda")
        .checkShowingAmount("£19.99")
        .checkAmountHasColor(TextColor.GREEN)
        .checkLocalAmountHidden()
        .checkShowingAddress()
        .checkDeclineReasonHidden()
        .checkNotesHidden()
        .checkHistoryHidden();
  }

  @Test public void online() {
    openDetailsScreen(atMerchant(AMAZON).withAmount(-50_00));

    DetailsScreen.onDetails()
        .checkShowingToolbarWithTitle("Amazon")
        .checkMapHidden()
        .checkShowingTitle("Amazon")
        .checkShowingAmount("£50.00")
        .checkAmountHasColor(TextColor.BLACK)
        .checkLocalAmountHidden()
        .checkAddressHidden()
        .checkDeclineReasonHidden()
        .checkNotesHidden()
        .checkShowingMerchantHistory("1", "£50.00", "£50.00");
  }

  @Test public void declinedBlocked() {
    checkDeclineReason(DeclineReason.CARD_BLOCKED, "Declined because your card was blocked");
  }

  @Test public void declinedCardInactive() {
    checkDeclineReason(DeclineReason.CARD_INACTIVE, "Declined because your card was frozen");
  }

  @Test public void declinedInsufficientFunds() {
    checkDeclineReason(DeclineReason.INSUFFICIENT_FUNDS, "Declined because you had insufficient funds");
  }

  @Test public void declinedOther() {
    checkDeclineReason(DeclineReason.OTHER, "Declined");
  }

  private void checkDeclineReason(DeclineReason reason, String expectedDescription) {
    // A declined transaction is always excluded from spending
    openDetailsScreen(atMerchant(ASDA).withAmount(-50_00).withIncludeInSpending(false).withDeclineReason(reason));

    DetailsScreen.onDetails()
        .checkToolbarHidden()
        .checkShowingMap()
        .checkShowingTitle("Asda")
        .checkShowingAmount("£50.00")
        .checkAmountHasColor(TextColor.RED)
        .checkLocalAmountHidden()
        .checkShowingAddress()
        .checkShowingDeclineReason(expectedDescription)
        .checkNotesHidden()
        .checkHistoryHidden();
  }

  @Test public void hideAmount() {
    // A transaction whose amount should be hidden is always excluded from spending
    openDetailsScreen(atMerchant(ASDA).withHideAmount(true).withIncludeInSpending(false));

    DetailsScreen.onDetails()
        .checkToolbarHidden()
        .checkShowingMap()
        .checkShowingTitle("Asda")
        .checkAmountHidden()
        .checkLocalAmountHidden()
        .checkShowingAddress()
        .checkDeclineReasonHidden()
        .checkNotesHidden()
        .checkHistoryHidden();
  }

  @Test public void notes() {
    openDetailsScreen(atMerchant(STARBUCKS).withAmount(-44_44).withNotes("This is an expensive coffee"));

    DetailsScreen.onDetails()
        .checkToolbarHidden()
        .checkShowingMap()
        .checkShowingTitle("Starbucks")
        .checkShowingAmount("£44.44")
        .checkAmountHasColor(TextColor.BLACK)
        .checkLocalAmountHidden()
        .checkShowingAddress()
        .checkDeclineReasonHidden()
        .checkShowingNotes("This is an expensive coffee")
        .checkShowingMerchantHistory("1", "£44.44", "£44.44");
  }

  @Test public void clickingToolbarUp_navigatesToTransactionsScreen() {
    openDetailsScreen(TestTransactions.topUp());

    DetailsScreen.checkDisplayed();
    DetailsScreen.clickUp();

    TransactionsScreen.checkDisplayed();
  }

  @Test public void clickingMapOverlayUp_navigatesToTransactionsScreen() {
    openDetailsScreen(atMerchant(ASDA));

    DetailsScreen.checkDisplayed();
    DetailsScreen.clickUp();

    TransactionsScreen.checkDisplayed();
  }

  @Test public void pressingBack_navigatesToTransactionsScreen() {
    openDetailsScreen(atMerchant(ASDA));

    DetailsScreen.checkDisplayed();
    Espresso.pressBack();

    TransactionsScreen.checkDisplayed();
  }

  private void openDetailsScreen(TestTransaction... transactions) {
    MonzoServer.getInstance().onTransactionsRequestReturn(Arrays.asList(transactions));
    activityRule.launchActivity();
    TransactionsScreen.clickFirstTransaction();
  }
}
