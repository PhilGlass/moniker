package glass.phil.monzo.presentation.login;

import java.util.UUID;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import glass.phil.monzo.core.Generator;

@Module public abstract class LoginModule {
  @Provides @Reusable static Generator<String> stateGenerator() {
    return () -> UUID.randomUUID().toString();
  }

  @Binds abstract Browser browser(CustomTabsBrowser browser);
  @Binds abstract LoginContract.Presenter presenter(LoginPresenter presenter);
}
