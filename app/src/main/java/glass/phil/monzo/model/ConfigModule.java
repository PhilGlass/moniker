package glass.phil.monzo.model;

import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.model.transactions.DbName;
import okhttp3.HttpUrl;

@Module public abstract class ConfigModule {
  @Provides static HttpUrl baseUrl() {
    return HttpUrl.parse("https://api.monzo.com/");
  }

  @Provides @DbName static String dbName() {
    return "transactions";
  }
}
