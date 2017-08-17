package glass.phil.monzo.presentation.login;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.TestData;
import glass.phil.monzo.core.Generator;

@Module public abstract class TestLoginModule {
  @Provides static Generator<String> stateGenerator() {
    return () -> TestData.STATE;
  }

  @Binds abstract Browser browser(RecordingBrowser browser);
  @Binds abstract LoginContract.Presenter presenter(LoginPresenter presenter);
}
