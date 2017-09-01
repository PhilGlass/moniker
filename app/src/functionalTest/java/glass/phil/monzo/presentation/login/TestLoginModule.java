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

  @Provides static Browser browser() {
    return RecordingBrowser.getInstance();
  }

  @Binds abstract LoginContract.Presenter presenter(LoginPresenter presenter);
}
