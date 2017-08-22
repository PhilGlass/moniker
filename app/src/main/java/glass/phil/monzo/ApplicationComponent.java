package glass.phil.monzo;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import glass.phil.monzo.model.ClockModule;
import glass.phil.monzo.model.ConfigModule;
import glass.phil.monzo.model.ModelModule;
import glass.phil.monzo.model.StoreModule;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = {
    AndroidSupportInjectionModule.class,
    ApplicationModule.class,
    AndroidModule.class,
    ModelModule.class,
    ClockModule.class,
    ConfigModule.class,
    StoreModule.class
}) interface ApplicationComponent extends AndroidInjector<MonzoApp> {
  OkHttpClient okHttpClient();

  @Component.Builder abstract class Builder extends AndroidInjector.Builder<MonzoApp> {
    @Override public abstract ApplicationComponent build();
  }
}
