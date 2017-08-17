package glass.phil.monzo.presentation.transactions.details;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.threeten.bp.Clock;

import javax.inject.Inject;

import butterknife.BindView;
import glass.phil.monzo.R;
import glass.phil.monzo.core.Bundles;
import glass.phil.monzo.model.Clocks.Local;
import glass.phil.monzo.model.transactions.Transaction;
import glass.phil.monzo.model.transactions.Transaction.Merchant.Address;
import glass.phil.monzo.model.transactions.TransactionHistory;
import glass.phil.monzo.presentation.base.BaseFragment;
import glass.phil.monzo.presentation.transactions.details.DetailsContract.DetailsView;
import glass.phil.monzo.presentation.transactions.details.DetailsContract.Presenter;
import glass.phil.monzo.presentation.transactions.details.DetailsContract.ViewModel;
import glass.phil.monzo.presentation.util.Categories;
import glass.phil.monzo.presentation.util.CurrencyFormatter;
import glass.phil.monzo.presentation.util.DateFormatter;
import glass.phil.monzo.presentation.util.DeclineReasons;
import glass.phil.monzo.presentation.util.Outlines;

import static glass.phil.monzo.presentation.util.TextViews.showText;

public final class DetailsFragment extends BaseFragment<DetailsView, Presenter> implements DetailsView {
  private static final String KEY_TRANSACTION = "key::transaction";

  @BindView(R.id.detail_logo) ImageView logo;
  @BindView(R.id.detail_amount) TextView amount;
  @BindView(R.id.detail_local_amount) TextView localAmount;
  @BindView(R.id.detail_title) TextView title;
  @BindView(R.id.detail_address) TextView address;
  @BindView(R.id.detail_declined) TextView declined;
  @BindView(R.id.detail_notes) TextView notes;
  @BindView(R.id.detail_time) TextView time;

  @BindView(R.id.detail_history) View historyContainer;
  @BindView(R.id.detail_transactions_label) TextView transactionsLabel;
  @BindView(R.id.detail_transactions) TextView transactions;
  @BindView(R.id.detail_average_label) TextView averageLabel;
  @BindView(R.id.detail_average) TextView average;
  @BindView(R.id.detail_total_label) TextView totalLabel;
  @BindView(R.id.detail_total) TextView total;

  @Inject Transaction transaction;
  @Inject @Local Clock clock;

  public static DetailsFragment newInstance(Transaction transaction) {
    final DetailsFragment fragment = new DetailsFragment();
    fragment.setArguments(Bundles.just(KEY_TRANSACTION, transaction));
    return fragment;
  }

  Transaction transaction() {
    return getArguments().getParcelable(KEY_TRANSACTION);
  }

  @Override protected int layout() {
    if (showLocation(transaction.merchant())) {
      return R.layout.details_with_map;
    }
    return R.layout.details;
  }

  private boolean showLocation(@Nullable Transaction.Merchant merchant) {
    return merchant != null && !merchant.online();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.setId(R.id.details_screen);
    // Z order this screen above the transaction list, so it is always on top during animations
    view.setTranslationZ(view.getTranslationZ() + 1);
    logo.setOutlineProvider(Outlines.roundRect(getResources().getDimension(R.dimen.detail_logo_corner_radius)));
    logo.setClipToOutline(true);
    bindToolbar();
    bindTransaction();
  }

  @Override protected DetailsView view() {
    return this;
  }

  @Override public void render(ViewModel model) {
    if (!model.loading()) {
      final TransactionHistory history = model.history();
      if (history == null) {
        historyContainer.setVisibility(View.GONE);
      } else {
        historyContainer.setVisibility(View.VISIBLE);
        transactions.setText(String.valueOf(history.transactionCount()));
        average.setText(CurrencyFormatter.unformatted(getActivity(), history.averageSpend()));
        total.setText(CurrencyFormatter.unformatted(getActivity(), history.totalSpend()));
      }
    }
  }

  private void bindToolbar() {
    final Toolbar toolbar = (Toolbar) requireView().findViewById(R.id.detail_toolbar);
    if (toolbar != null) {
      if (transaction.topUp()) {
        toolbar.setTitle(R.string.top_up);
      } else {
        toolbar.setTitle(transaction.requireMerchant().name());
      }
      toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    } else {
      requireView().findViewById(R.id.detail_up).setOnClickListener(v -> getFragmentManager().popBackStack());
    }
  }

  private void bindTransaction() {
    if (transaction.topUp()) {
      Glide.clear(logo);
      logo.setImageResource(R.drawable.ic_top_up);
      title.setText(R.string.top_up);
    } else {
      Glide.with(getActivity())
          .load(transaction.requireMerchant().logoUrl())
          .placeholder(Categories.iconFor(transaction.category()))
          .into(logo);
      title.setText(transaction.requireMerchant().name());
    }

    if (transaction.declined()) {
      showText(declined, DeclineReasons.declineReason(transaction.declineReason()));
    } else {
      declined.setVisibility(View.GONE);
    }

    if (transaction.notes() != null) {
      showText(notes, transaction.notes());
    } else {
      notes.setVisibility(View.GONE);
    }

    time.setText(DateFormatter.formatDateTime(getActivity(), clock, transaction.created()));

    if (transaction.hideAmount()) {
      amount.setVisibility(View.GONE);
      localAmount.setVisibility(View.GONE);
    } else {
      showText(amount, CurrencyFormatter.formatDetail(getActivity(), transaction.amount(), transaction.declined()));
      if (transaction.inForeignCurrency()) {
        showText(localAmount, CurrencyFormatter.formatLocal(getActivity(), transaction.localAmount()));
      } else {
        localAmount.setVisibility(View.GONE);
      }
    }

    if (!showLocation(transaction.merchant())) {
      address.setVisibility(View.GONE);
    } else {
      showText(address, transaction.requireMerchant().address().formattedAddress());
      bindMap();
    }

    transactionsLabel.setText(transaction.topUp() ? R.string.top_ups : R.string.num_transactions);
    averageLabel.setText(transaction.topUp() ? R.string.average_top_up : R.string.average_spend);
    totalLabel.setText(transaction.topUp() ? R.string.total_top_up : R.string.total_spend);
  }

  // To ensure a smooth configuration change, our map fragment needs to be retained. When the new activity is created,
  // its fragment manager will attempt to attach the retained fragment to an appropriate container view, throwing an
  // exception if the new activity's content view does not include such a container. This means that every variant of
  // the details layout must include a map fragment container, even if that container is not visible.
  private void bindMap() {
    SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.detail_map);
    if (fragment != null) {
      return;
    }

    final Address address = transaction.requireMerchant().address();
    final LatLng position = new LatLng(address.latitude(), address.longitude());
    final GoogleMapOptions options = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(position, 15));
    fragment = SupportMapFragment.newInstance(options);
    fragment.setRetainInstance(true);
    getChildFragmentManager().beginTransaction()
        // Works around child fragments immediately disappearing when their parent's exit
        // animation begins: https://stackoverflow.com/q/14900738
        .setCustomAnimations(R.anim.no_op, R.anim.no_op)
        .add(R.id.detail_map, fragment)
        .commit();

    fragment.getMapAsync(map -> {
      final float[] hsv = new float[3];
      Color.colorToHSV(getActivity().getColor(R.color.colorPrimary), hsv);
      map.addMarker(new MarkerOptions().position(position)
          .icon(BitmapDescriptorFactory.defaultMarker(hsv[0]))
          .title(transaction.requireMerchant().name())
          .snippet(CurrencyFormatter.unformatted(getActivity(), transaction.amount())));
    });
  }
}
