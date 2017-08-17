package glass.phil.monzo.presentation.transactions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar.BaseCallback;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.Clock;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import glass.phil.monzo.R;
import glass.phil.monzo.model.Clocks.Local;
import glass.phil.monzo.model.balance.BalanceManager.Balance;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.presentation.base.BaseFragment;
import glass.phil.monzo.presentation.transactions.TransactionsContract.Presenter;
import glass.phil.monzo.presentation.transactions.TransactionsContract.TransactionsView;
import glass.phil.monzo.presentation.transactions.TransactionsContract.ViewModel;
import glass.phil.monzo.presentation.transactions.details.DetailsFragment;

import static glass.phil.monzo.presentation.util.CurrencyFormatter.formatBalance;

public final class TransactionsFragment extends BaseFragment<TransactionsView, Presenter> implements TransactionsView {
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.balance) TextView balanceView;
  @BindView(R.id.spent_today) TextView spentTodayView;
  @BindView(R.id.transactions_recycler) RecyclerView recycler;
  @BindView(R.id.transactions_loading) View loading;
  @BindView(R.id.transactions_empty) View empty;

  @Inject @Local Clock clock;

  private TransactionsAdapter adapter;
  private Snackbar snackbar;
  private View visibleView;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar.setTitle(R.string.transactions);
    adapter = new TransactionsAdapter(getActivity(), clock, this::showDetails);
    recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    recycler.addItemDecoration(new StickyHeaderDecoration(adapter));
    recycler.addItemDecoration(new TransactionDividerDecoration(getActivity(), adapter));
    recycler.setHasFixedSize(true);

    snackbar = Snackbar.make(recycler, R.string.refresh_error, Snackbar.LENGTH_LONG)
        .addCallback(new SnackbarCallback())
        .setAction(R.string.try_again, v -> presenter.retry());
  }

  private void showDetails(Transaction transaction) {
    getFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.slide_up_from_bottom, R.anim.no_op, R.anim.no_op, R.anim.slide_down_from_top)
        .replace(android.R.id.content, DetailsFragment.newInstance(transaction))
        .addToBackStack(null)
        .commit();
  }

  @Override protected int layout() {
    return R.layout.transactions;
  }

  @Override protected TransactionsView view() {
    return this;
  }

  @Override public void render(ViewModel model) {
    renderBalance(model.balance());
    renderTransactions(model.transactions());
    renderError(model.error());
  }

  private void renderBalance(@Nullable Balance balance) {
    balanceView.setText(balance == null ? null : formatBalance(getActivity(), balance.balance()));
    spentTodayView.setText(balance == null ? null : formatBalance(getActivity(), balance.spentToday()));
  }

  private void renderTransactions(@Nullable List<Transaction> transactions) {
    if (transactions == null) {
      showView(loading);
    } else {
      if (transactions.isEmpty()) {
        adapter.clear();
        showView(empty);
      } else {
        adapter.replaceAll(transactions);
        showView(recycler);
      }
      // Defer setting the adapter until we've received our first update, so scroll position is correctly restored
      if (recycler.getAdapter() == null) {
        recycler.setAdapter(adapter);
      }
    }
  }

  private void showView(View view) {
    if (view != visibleView) {
      TransitionManager.beginDelayedTransition((ViewGroup) requireView(), new Fade());
      recycler.setVisibility(view == recycler ? View.VISIBLE : View.INVISIBLE);
      loading.setVisibility(view == loading ? View.VISIBLE : View.INVISIBLE);
      empty.setVisibility(view == empty ? View.VISIBLE : View.INVISIBLE);
      visibleView = view;
    }
  }

  private void renderError(boolean showError) {
    if (showError) {
      snackbar.show();
    } else {
      snackbar.dismiss();
    }
  }

  private class SnackbarCallback extends BaseCallback<Snackbar> {
    @Override public void onDismissed(Snackbar transientBottomBar, int event) {
      if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
        presenter.onErrorDismissed();
      }
    }
  }
}
