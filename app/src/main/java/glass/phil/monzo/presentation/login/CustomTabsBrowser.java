package glass.phil.monzo.presentation.login;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

import javax.inject.Inject;

import okhttp3.HttpUrl;

final class CustomTabsBrowser extends CustomTabsServiceConnection implements Browser {
  private final Activity activity;

  private Uri uri;
  private CustomTabsSession tabsSession;

  @Inject CustomTabsBrowser(Activity activity) {
    this.activity = activity;
  }

  @Override public void connect() {
    final String tabsPackage = CustomTabsClient.getPackageName(activity, null);
    CustomTabsClient.bindCustomTabsService(activity, tabsPackage, this);
  }

  @Override public void loadPage(HttpUrl url) {
    uri = Uri.parse(url.toString());
    tryLoad();
  }

  @Override public void showPage() {
    final CustomTabsIntent tabsIntent = new CustomTabsIntent.Builder(tabsSession).build();
    tabsIntent.launchUrl(activity, uri);
  }

  @Override public void disconnect() {
    activity.unbindService(this);
  }

  @Override public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
    client.warmup(0);
    tabsSession = client.newSession(null);
    if (uri != null) {
      tryLoad();
    }
  }

  @Override public void onServiceDisconnected(ComponentName name) {
    tabsSession = null;
  }

  private void tryLoad() {
    if (tabsSession != null) {
      tabsSession.mayLaunchUrl(uri, null, null);
    }
  }
}
