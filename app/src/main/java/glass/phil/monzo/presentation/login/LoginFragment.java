package glass.phil.monzo.presentation.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import glass.phil.monzo.R;
import glass.phil.monzo.presentation.base.BaseFragment;
import glass.phil.monzo.presentation.login.LoginContract.LoginView;
import glass.phil.monzo.presentation.login.LoginContract.Presenter;
import glass.phil.monzo.presentation.transactions.TransactionsFragment;
import glass.phil.monzo.presentation.util.Intents;
import glass.phil.monzo.presentation.util.Windows;

public final class LoginFragment extends BaseFragment<LoginView, Presenter> implements LoginView {
  private static final Uri STORE_URI = Uri.parse("market://details?id=co.uk.getmondo");

  @BindView(R.id.form) View form;
  @BindView(R.id.loading) View loading;
  @BindView(R.id.error) View error;

  @Inject Browser browser;

  private String currentState;
  private View visibleView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    browser.connect();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Windows.setColorPrimaryBackground(getActivity());
  }

  @Override public void onResume() {
    super.onResume();
    final Intent intent = getActivity().getIntent();
    if (Intents.hasAction(intent, Intent.ACTION_VIEW)) {
      final Uri uri = intent.getData();
      presenter.onLoginResult(uri.getQueryParameter("code"), uri.getQueryParameter("state"));
      // Ensure we handle this redirect only once
      getActivity().setIntent(null);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    Windows.setDefaultBackground(getActivity());
  }

  @Override public void onDestroy() {
    super.onDestroy();
    browser.disconnect();
  }

  @Override protected int layout() {
    return R.layout.login;
  }

  @Override protected LoginView view() {
    return this;
  }

  @Override public void render(LoginContract.ViewModel model) {
    if (model.loginComplete()) {
      goToTransactions();
    } else {
      loadPage(model.state());
      if (model.loading()) {
        showView(loading);
      } else if (model.error()) {
        showView(error);
      } else {
        showView(form);
      }
    }
  }

  private void goToTransactions() {
    getFragmentManager().beginTransaction()
        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
        .replace(android.R.id.content, new TransactionsFragment())
        .commit();
  }

  private void loadPage(String state) {
    if (!state.equals(currentState)) {
      browser.loadPage(OauthUrl.withState(state));
      currentState = state;
    }
  }

  private void showView(View view) {
    if (view != visibleView) {
      TransitionManager.beginDelayedTransition((ViewGroup) requireView(), new Fade());
      form.setVisibility(view == form ? View.VISIBLE : View.INVISIBLE);
      loading.setVisibility(view == loading ? View.VISIBLE : View.INVISIBLE);
      error.setVisibility(view == error ? View.VISIBLE : View.INVISIBLE);
      visibleView = view;
    }
  }

  @OnClick(R.id.log_in) void loginClicked() {
    browser.showPage();
  }

  @OnClick(R.id.sign_up) void signUpClicked() {
    startActivity(new Intent(Intent.ACTION_VIEW).setData(STORE_URI));
  }

  @OnClick(R.id.retry) void retryClicked() {
    presenter.retry();
  }
}
