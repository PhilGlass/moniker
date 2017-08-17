package glass.phil.monzo.model.auth;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import glass.phil.monzo.core.rx.Unit;
import glass.phil.monzo.model.Store;
import retrofit2.Retrofit;

@Module public abstract class AuthModule {
  @Provides @Singleton static Relay<Unit> authRelay() {
    return PublishRelay.create();
  }

  @Provides static AuthApi authApi(Retrofit retrofit) {
    return retrofit.create(AuthApi.class);
  }

  @Provides @Singleton static Store<AuthManager.Token> tokenStore(Store.Factory factory) {
    return factory.create(AuthManager.Token.class, "store::auth_token");
  }

  @Binds @Singleton abstract AuthManager authManager(AuthManagerImpl authManager);
}
