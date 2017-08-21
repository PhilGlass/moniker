package glass.phil.monzo;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule public final class MonzoGlideModule extends AppGlideModule {
  @Override public void applyOptions(Context context, GlideBuilder builder) {
    builder.setLogLevel(Log.ERROR);
  }
}
