package glass.phil.monzo.model;

import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.model.transactions.DbName;
import glass.phil.monzo.test.server.MonzoServer;
import okhttp3.HttpUrl;

@Module public abstract class TestConfigModule {
  @Provides static HttpUrl baseUrl() {
    return MonzoServer.getInstance().url("/");
  }

  @Provides @DbName @Nullable static String dbName() {
    // Create an in-memory database
    return null;
  }
}
