package glass.phil.monzo;

import com.squareup.sqlbrite2.BriteDatabase;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import glass.phil.monzo.model.ModelModule;
import glass.phil.monzo.model.TestClockModule;
import glass.phil.monzo.model.TestConfigModule;
import glass.phil.monzo.model.TestStoreModule;
import glass.phil.monzo.test.DetailsScreenTest;
import glass.phil.monzo.test.LoginScreenTest;
import glass.phil.monzo.test.MainActivityTest;
import glass.phil.monzo.test.TransactionsScreenTest;

@Singleton
@Component(modules = {
    AndroidSupportInjectionModule.class,
    TestApplicationModule.class,
    AndroidModule.class,
    ModelModule.class,
    TestClockModule.class,
    TestConfigModule.class,
    TestStoreModule.class
})
@SuppressWarnings("WeakerAccess")
public interface TestApplicationComponent extends AndroidInjector<FunctionalTestApp> {
  void inject(MainActivityTest test);
  void inject(LoginScreenTest test);
  void inject(TransactionsScreenTest test);
  void inject(DetailsScreenTest test);

  BriteDatabase transactionsDb();

  @Component.Builder abstract class Builder extends AndroidInjector.Builder<FunctionalTestApp> {
    @Override public abstract TestApplicationComponent build();
  }
}
