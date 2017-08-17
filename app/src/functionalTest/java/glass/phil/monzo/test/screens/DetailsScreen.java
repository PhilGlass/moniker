package glass.phil.monzo.test.screens;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import glass.phil.monzo.R;
import glass.phil.monzo.core.Objects;
import glass.phil.monzo.test.util.MoreViewMatchers.TextColor;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static glass.phil.monzo.test.util.MoreViewMatchers.displayedWithText;
import static glass.phil.monzo.test.util.MoreViewMatchers.doesNotExistOrNotDisplayed;
import static glass.phil.monzo.test.util.MoreViewMatchers.withTextColor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;

public final class DetailsScreen {
  public static void checkDisplayed() {
    onView(withId(R.id.details_screen)).check(matches(isDisplayed()));
  }

  public static void clickUp() {
    final Matcher<View> upMatcher = anyOf(
        allOf(withParent(withId(R.id.detail_toolbar)), withContentDescription(R.string.up)),
        withId(R.id.detail_up)
    );
    onView(upMatcher).perform(click());
  }

  public static DetailsScreen onDetails() {
    return new DetailsScreen();
  }

  private DetailsScreen() {}

  public DetailsScreen checkShowingToolbarWithTitle(String title) {
    onView(withId(R.id.detail_toolbar)).check(matches(allOf(isDisplayed(), isToolbarWithTitle(title))));
    return this;
  }

  public DetailsScreen checkToolbarHidden() {
    onView(withId(R.id.detail_toolbar)).check(doesNotExistOrNotDisplayed());
    return this;
  }

  public DetailsScreen checkShowingMap() {
    onView(withId(R.id.detail_map)).check(matches(isDisplayed()));
    return this;
  }

  public DetailsScreen checkMapHidden() {
    onView(withId(R.id.detail_map)).check(doesNotExistOrNotDisplayed());
    return this;
  }

  public DetailsScreen checkShowingTitle(String title) {
    onView(withId(R.id.detail_title)).check(matches(displayedWithText(title)));
    return this;
  }

  public DetailsScreen checkShowingAmount(String amount) {
    onView(withId(R.id.detail_amount)).check(matches(displayedWithText(amount)));
    return this;
  }

  public DetailsScreen checkAmountHasColor(TextColor textColor) {
    onView(withId(R.id.detail_amount)).check(matches(allOf(isDisplayed(), withTextColor(textColor))));
    return this;
  }

  public DetailsScreen checkAmountHidden() {
    onView(withId(R.id.detail_amount)).check(matches(not(isDisplayed())));
    return this;
  }

  public DetailsScreen checkShowingLocalAmount(String amount) {
    onView(withId(R.id.detail_local_amount)).check(matches(displayedWithText(amount)));
    return this;
  }

  public DetailsScreen checkLocalAmountHidden() {
    onView(withId(R.id.detail_local_amount)).check(matches(not(isDisplayed())));
    return this;
  }

  public DetailsScreen checkShowingAddress() {
    onView(withId(R.id.detail_address)).check(matches(isDisplayed()));
    return this;
  }

  public DetailsScreen checkAddressHidden() {
    onView(withId(R.id.detail_address)).check(matches(not(isDisplayed())));
    return this;
  }

  public DetailsScreen checkShowingDeclineReason(String description) {
    onView(withId(R.id.detail_declined)).check(matches(displayedWithText(description)));
    return this;
  }

  public DetailsScreen checkDeclineReasonHidden() {
    onView(withId(R.id.detail_declined)).check(matches(not(isDisplayed())));
    return this;
  }

  public DetailsScreen checkShowingNotes(String notes) {
    onView(withId(R.id.detail_notes)).check(matches(displayedWithText(notes)));
    return this;
  }

  public DetailsScreen checkNotesHidden() {
    onView(withId(R.id.detail_notes)).check(matches(not(isDisplayed())));
    return this;
  }

  public DetailsScreen checkShowingTopUpHistory(String topUps, String averageTopUp, String totalToppedUp) {
    onView(withId(R.id.detail_transactions_label)).check(matches(displayedWithText(R.string.top_ups)));
    onView(withId(R.id.detail_transactions)).check(matches(displayedWithText(topUps)));
    onView(withId(R.id.detail_average_label)).check(matches(displayedWithText(R.string.average_top_up)));
    onView(withId(R.id.detail_average)).check(matches(displayedWithText(averageTopUp)));
    onView(withId(R.id.detail_total_label)).check(matches(displayedWithText(R.string.total_top_up)));
    onView(withId(R.id.detail_total)).check(matches(displayedWithText(totalToppedUp)));
    return this;
  }

  public DetailsScreen checkShowingMerchantHistory(String transactions, String averageSpend, String totalSpent) {
    onView(withId(R.id.detail_transactions_label)).check(matches(displayedWithText(R.string.transactions)));
    onView(withId(R.id.detail_transactions)).check(matches(displayedWithText(transactions)));
    onView(withId(R.id.detail_average_label)).check(matches(displayedWithText(R.string.average_spend)));
    onView(withId(R.id.detail_average)).check(matches(displayedWithText(averageSpend)));
    onView(withId(R.id.detail_total_label)).check(matches(displayedWithText(R.string.total_spend)));
    onView(withId(R.id.detail_total)).check(matches(displayedWithText(totalSpent)));
    return this;
  }

  public DetailsScreen checkHistoryHidden() {
    onView(withId(R.id.detail_transactions_label)).check(matches(not(isDisplayed())));
    onView(withId(R.id.detail_transactions)).check(matches(not(isDisplayed())));
    onView(withId(R.id.detail_average_label)).check(matches(not(isDisplayed())));
    onView(withId(R.id.detail_average)).check(matches(not(isDisplayed())));
    onView(withId(R.id.detail_total_label)).check(matches(not(isDisplayed())));
    onView(withId(R.id.detail_total)).check(matches(not(isDisplayed())));
    return this;
  }

  private static Matcher<View> isToolbarWithTitle(String title) {
    return new BoundedMatcher<View, Toolbar>(Toolbar.class) {
      @Override protected boolean matchesSafely(Toolbar item) {
        return Objects.equal(item.getTitle(), title);
      }

      @Override public void describeTo(Description description) {
        description.appendText("is toolbar with title ").appendValue(title);
      }
    };
  }
}
