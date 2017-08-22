package glass.phil.monzo;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;

@GlideModule
@Excludes(OkHttpLibraryGlideModule.class)
public final class MonzoGlideModule extends AppGlideModule {
  @Override public boolean isManifestParsingEnabled() {
    return false;
  }

  @Override public void applyOptions(Context context, GlideBuilder builder) {
    builder.setLogLevel(Log.ERROR);
  }

  @Override public void registerComponents(Context context, Glide glide, Registry registry) {
    final OkHttpClient client = MonzoApp.getComponent(context).okHttpClient();
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
  }
}
