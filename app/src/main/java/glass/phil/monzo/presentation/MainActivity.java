package glass.phil.monzo.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jakewharton.rxrelay2.Relay;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import glass.phil.monzo.R;
import glass.phil.monzo.core.rx.Unit;
import glass.phil.monzo.model.auth.AuthManager;
import glass.phil.monzo.presentation.base.PresenterStore;
import glass.phil.monzo.presentation.login.LoginFragment;
import glass.phil.monzo.presentation.transactions.TransactionsFragment;
import glass.phil.monzo.presentation.util.Windows;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class MainActivity extends DaggerAppCompatActivity {
  @Inject AuthManager authManager;
  @Inject Relay<Unit> authRelay;

  private Disposable disposable;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Windows.setDefaultBackground(this);
    PresenterStore.install(this);

    if (savedInstanceState == null) {
      final Fragment targetFragment = loggedIn() ? new TransactionsFragment() : new LoginFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(android.R.id.content, targetFragment)
          .commit();
    }
  }

  private boolean loggedIn() {
    return authManager.currentToken() != null;
  }

  @Override protected void onNewIntent(Intent intent) {
    setIntent(intent);
  }

  @Override protected void onStart() {
    super.onStart();
    disposable = authRelay.observeOn(AndroidSchedulers.mainThread()).subscribe(it -> showLogin());
  }

  private void showLogin() {
    getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        .replace(android.R.id.content, new LoginFragment())
        .commit();
  }

  @Override protected void onStop() {
    super.onStop();
    disposable.dispose();
  }
}
