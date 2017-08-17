package glass.phil.monzo.model;

import java.lang.reflect.Type;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module public abstract class TestStoreModule {
  @Provides @Reusable static Store.Factory storeFactory() {
    return new Store.Factory() {
      @Override public <T> Store<T> create(Type type, String key) {
        return new InMemoryStore<>();
      }
    };
  }
}
