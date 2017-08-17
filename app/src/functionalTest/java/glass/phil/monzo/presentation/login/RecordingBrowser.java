package glass.phil.monzo.presentation.login;

import android.app.Activity;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.HttpUrl;

import static org.assertj.core.api.Java6Assertions.assertThat;

public final class RecordingBrowser implements Browser {
  // Assume we have two tests, A and B, which run sequentially. As of release 0.5, ActivityTestRule does not guarantee
  // that Activity A's onDestroy() is called before Activity B's onCreate(). This means that if this class were a
  // process-wide singleton, it would be possible for Activity A to call disconnect() *after* Activity B has called
  // connect(), leaving the browser in an unexpected state. See: https://issuetracker.google.com/issues/37082857
  private static final Map<Activity, RecordingBrowser> INSTANCES = new HashMap<>();

  static {
    ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(((activity, stage) -> {
      if (stage == Stage.DESTROYED) {
        INSTANCES.remove(activity);
      }
    }));
  }

  public static RecordingBrowser getInstance(Activity activity) {
    return INSTANCES.get(activity);
  }

  private volatile boolean connected;
  private volatile HttpUrl loadedUrl;
  private volatile boolean pageShown;

  @Inject RecordingBrowser(Activity activity) {
    INSTANCES.put(activity, this);
  }

  @Override public void connect() {
    if (connected) {
      throw new IllegalStateException();
    }
    connected = true;
  }

  @Override public void loadPage(HttpUrl url) {
    if (!connected) {
      throw new IllegalStateException();
    }
    loadedUrl = url;
  }

  @Override public void showPage() {
    if (!connected || loadedUrl == null) {
      throw new IllegalStateException();
    }
    pageShown = true;
  }

  @Override public void disconnect() {
    if (!connected) {
      throw new IllegalStateException();
    }
    connected = false;
    loadedUrl = null;
    pageShown = false;
  }

  public void assertPageLoaded(HttpUrl url) {
    assertThat(connected).isTrue();
    assertThat(loadedUrl).isEqualTo(url);
    assertThat(pageShown).isTrue();
  }
}
