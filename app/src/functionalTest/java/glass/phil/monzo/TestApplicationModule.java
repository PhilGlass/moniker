package glass.phil.monzo;

import android.app.Application;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import glass.phil.monzo.presentation.MainActivity;
import glass.phil.monzo.presentation.TestMainActivityModule;

@Module abstract class TestApplicationModule {
  @Binds abstract Application application(MonzoApp application);

  @ContributesAndroidInjector(modules = TestMainActivityModule.class)
  abstract MainActivity mainActivity();
}
