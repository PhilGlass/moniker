package glass.phil.monzo.presentation;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import glass.phil.monzo.presentation.login.LoginFragment;
import glass.phil.monzo.presentation.login.LoginModule;
import glass.phil.monzo.presentation.transactions.TransactionsFragment;
import glass.phil.monzo.presentation.transactions.TransactionsModule;
import glass.phil.monzo.presentation.transactions.details.DetailsFragment;
import glass.phil.monzo.presentation.transactions.details.DetailsModule;

@Module public abstract class MainActivityModule {
  @Binds abstract Activity activity(MainActivity activity);

  @ContributesAndroidInjector(modules = LoginModule.class)
  abstract LoginFragment loginFragment();

  @ContributesAndroidInjector(modules = TransactionsModule.class)
  abstract TransactionsFragment transactionsFragment();

  @ContributesAndroidInjector(modules = DetailsModule.class)
  abstract DetailsFragment detailsFragment();
}
