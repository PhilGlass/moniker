package glass.phil.monzo.presentation.transactions;

import android.app.Activity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import org.threeten.bp.Clock;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import glass.phil.monzo.GlideRequests;
import glass.phil.monzo.R;
import glass.phil.monzo.model.Clocks.Local;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.presentation.util.Categories;
import glass.phil.monzo.presentation.util.CurrencyFormatter;
import glass.phil.monzo.presentation.util.DateFormatter;
import glass.phil.monzo.presentation.util.DeclineReasons;
import glass.phil.monzo.presentation.util.Outlines;
import io.reactivex.Observable;

import static glass.phil.monzo.presentation.util.TextViews.showText;

final class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionHolder> implements
    StickyHeaderAdapter<TransactionsAdapter.HeaderHolder> {
  private final Clock clock;
  private final LayoutInflater inflater;
  private final ViewOutlineProvider outlineProvider;
  private final GlideRequests glide;
  private final Relay<Transaction> itemClicks = PublishRelay.create();

  private List<Transaction> transactions = Collections.emptyList();

  @Inject TransactionsAdapter(Activity activity, @Local Clock clock, GlideRequests glide) {
    this.clock = clock;
    this.glide = glide;
    inflater = LayoutInflater.from(activity);
    outlineProvider = Outlines.roundRect(activity.getResources().getDimension(R.dimen.transactions_logo_corner_radius));
  }

  void clear() {
    final int transactionCount = transactions.size();
    transactions = Collections.emptyList();
    notifyItemRangeRemoved(0, transactionCount);
  }

  void replaceAll(List<Transaction> newTransactions) {
    final DiffCallback callback = new DiffCallback(transactions, newTransactions);
    transactions = newTransactions;
    DiffUtil.calculateDiff(callback, false).dispatchUpdatesTo(this);
  }

  Observable<Transaction> itemClicks() {
    return itemClicks;
  }

  @Override public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new TransactionHolder(inflater.inflate(R.layout.transaction_item, parent, false));
  }

  @Override public void onBindViewHolder(TransactionHolder holder, int position) {
    holder.bind(transactions.get(position));
  }

  @Override public int getItemCount() {
    return transactions.size();
  }

  @Override public long getHeaderId(int position) {
    final Transaction transaction = transactions.get(position);
    return transaction.created().atZone(clock.getZone()).toLocalDate().toEpochDay();
  }

  @Override public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
    return new HeaderHolder(inflater.inflate(R.layout.date_header, parent, false));
  }

  @Override public void onBindHeaderViewHolder(HeaderHolder holder, int position) {
    holder.bind(transactions.get(position));
  }

  final class TransactionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.transaction_logo) ImageView logo;
    @BindView(R.id.transaction_title) TextView title;
    @BindView(R.id.transaction_notes) TextView notes;
    @BindView(R.id.transaction_amount) TextView amount;
    @BindView(R.id.transaction_local_amount) TextView localAmount;

    private Transaction transaction;

    TransactionHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
      logo.setOutlineProvider(outlineProvider);
      logo.setClipToOutline(true);
    }

    void bind(Transaction transaction) {
      this.transaction = transaction;
      if (transaction.topUp()) {
        glide.clear(logo);
        logo.setImageResource(R.drawable.ic_top_up);
        title.setText(R.string.top_up);
      } else {
        glide.load(transaction.requireMerchant().logoUrl())
            .placeholder(Categories.iconFor(transaction.category()))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(logo);
        title.setText(transaction.requireMerchant().name());
      }

      if (transaction.notes() != null) {
        showText(notes, transaction.notes());
      } else {
        notes.setVisibility(View.GONE);
      }

      if (transaction.declined()) {
        showText(amount, DeclineReasons.declined(itemView.getContext()));
        localAmount.setVisibility(View.GONE);
      } else if (transaction.hideAmount()) {
        amount.setVisibility(View.GONE);
        localAmount.setVisibility(View.GONE);
      } else {
        showText(amount, CurrencyFormatter.formatTransaction(itemView.getContext(), transaction.amount()));
        if (transaction.inForeignCurrency()) {
          showText(localAmount, CurrencyFormatter.formatLocal(itemView.getContext(), transaction.localAmount()));
        } else {
          localAmount.setVisibility(View.GONE);
        }
      }
    }

    @Override public void onClick(View v) {
      itemClicks.accept(transaction);
    }
  }

  final class HeaderHolder extends RecyclerView.ViewHolder {
    HeaderHolder(View itemView) {
      super(itemView);
    }

    void bind(Transaction transaction) {
      ((TextView) itemView).setText(DateFormatter.formatDate(itemView.getContext(), clock, transaction.created()));
    }
  }

  private static final class DiffCallback extends DiffUtil.Callback {
    private final List<Transaction> oldList;
    private final List<Transaction> newList;

    DiffCallback(List<Transaction> oldList, List<Transaction> newList) {
      this.oldList = oldList;
      this.newList = newList;
    }

    @Override public int getOldListSize() {
      return oldList.size();
    }

    @Override public int getNewListSize() {
      return newList.size();
    }

    @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
      return oldList.get(oldItemPosition).id().equals(newList.get(newItemPosition).id());
    }

    @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
      return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
  }
}
