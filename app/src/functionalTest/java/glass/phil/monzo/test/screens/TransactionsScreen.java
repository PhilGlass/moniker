package glass.phil.monzo.test.screens;

import android.support.annotation.Nullable;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import glass.phil.monzo.R;
import glass.phil.monzo.test.util.MoreViewMatchers.TextColor;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static glass.phil.monzo.test.util.MoreViewMatchers.displayedWithText;
import static glass.phil.monzo.test.util.MoreViewMatchers.withTextColor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public final class TransactionsScreen {
  public static void checkDisplayed() {
    onView(withId(R.id.transactions_screen)).check(matches(isDisplayed()));
  }

  public static void checkShowingBalance(String balance, String spentToday) {
    onView(withId(R.id.balance)).check(matches(allOf(isDisplayed(), withText(balance))));
    onView(withId(R.id.spent_today)).check(matches(allOf(isDisplayed(), withText(spentToday))));
  }

  public static void checkShowingEmpty() {
    onView(withId(R.id.transactions_empty)).check(matches(isDisplayed()));
  }

  public static void checkShowingTransactions(int count) {
    onView(withId(R.id.transactions_recycler)).check(matches(allOf(isDisplayed(), withItemCount(count))));
  }

  public static void clickFirstTransaction() {
    onView(withId(R.id.transactions_recycler)).perform(actionOnItemAtPosition(0, click()));
  }

  public static TransactionInteraction onFirstTransaction() {
    return new TransactionInteraction(0);
  }

  public static TransactionInteraction onTransactionAtPosition(int position) {
    return new TransactionInteraction(position);
  }

  private static Matcher<View> withItemCount(int count) {
    return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
      @Override protected boolean matchesSafely(RecyclerView item) {
        return item.getAdapter().getItemCount() == count;
      }

      @Override public void describeTo(Description description) {
        description.appendText("is RecyclerView with item count ").appendValue(count);
      }
    };
  }

  @SuppressWarnings("WeakerAccess")
  public static final class TransactionInteraction {
    private final int position;

    TransactionInteraction(int position) {
      this.position = position;
    }

    public TransactionInteraction checkShowingTitle(String title) {
      checkDescendant(R.id.transaction_title, displayedWithText(title));
      return this;
    }

    public TransactionInteraction checkShowingAmount(String amount) {
      checkDescendant(R.id.transaction_amount, displayedWithText(amount));
      return this;
    }

    public TransactionInteraction checkAmountHasColor(TextColor color) {
      checkDescendant(R.id.transaction_amount, withTextColor(color));
      return this;
    }

    public TransactionInteraction checkAmountHidden() {
      checkDescendant(R.id.transaction_amount, not(isDisplayed()));
      return this;
    }

    public TransactionInteraction checkShowingDeclined() {
      checkDescendant(R.id.transaction_amount,
          allOf(displayedWithText(R.string.declined), withTextColor(TextColor.RED)));
      return this;
    }

    public TransactionInteraction checkShowingLocalAmount(String amount) {
      checkDescendant(R.id.transaction_local_amount, displayedWithText(amount));
      return this;
    }

    public TransactionInteraction checkLocalAmountHidden() {
      checkDescendant(R.id.transaction_local_amount, not(isDisplayed()));
      return this;
    }

    public TransactionInteraction checkShowingNote(String note) {
      checkDescendant(R.id.transaction_notes, displayedWithText(note));
      return this;
    }

    public TransactionInteraction checkNoteHidden() {
      checkDescendant(R.id.transaction_notes, not(isDisplayed()));
      return this;
    }

    private void checkDescendant(int descendantId, Matcher<View> descendantAssertion) {
      onView(withId(R.id.transactions_recycler)).perform(RecyclerViewActions.scrollToPosition(position));
      onView(idMatcher(descendantId)).check(matches(descendantAssertion));
    }

    private Matcher<View> idMatcher(int descendantId) {
      return new TypeSafeMatcher<View>() {
        @Override protected boolean matchesSafely(View item) {
          return isDescendantOfExpectedItemView(item) && item.getId() == descendantId;
        }

        @Override public void describeTo(Description description) {
          description.appendText("is descendant of transactions recycler item view at position ")
              .appendValue(position)
              .appendText(" and has id ")
              .appendValue(descendantId);
        }
      };
    }

    private boolean isDescendantOfExpectedItemView(View view) {
      final RecyclerView transactionsRecycler = findTransactionsRecyclerParent(view);
      if (transactionsRecycler != null) {
        final View itemView = transactionsRecycler.findContainingItemView(view);
        return transactionsRecycler.getChildAdapterPosition(itemView) == position;
      }
      return false;
    }

    @Nullable private RecyclerView findTransactionsRecyclerParent(View view) {
      ViewParent parent = view.getParent();
      while (parent != null) {
        if (parent instanceof RecyclerView) {
          final RecyclerView recycler = (RecyclerView) parent;
          if (recycler.getId() == R.id.transactions_recycler) {
            return recycler;
          }
        }
        parent = parent.getParent();
      }
      return null;
    }
  }

  private TransactionsScreen() {
    throw new AssertionError("No instances");
  }
}
