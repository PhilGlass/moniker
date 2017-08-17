package glass.phil.monzo;

import android.app.Application;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import glass.phil.monzo.presentation.MainActivity;
import glass.phil.monzo.presentation.MainActivityModule;

@Module abstract class ApplicationModule {
  @Binds abstract Application application(MonzoApp application);

  @ContributesAndroidInjector(modules = MainActivityModule.class)
  abstract MainActivity mainActivity();
}
