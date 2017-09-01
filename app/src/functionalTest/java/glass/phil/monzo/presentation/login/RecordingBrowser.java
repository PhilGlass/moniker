package glass.phil.monzo.presentation.login;

import okhttp3.HttpUrl;

import static org.assertj.core.api.Java6Assertions.assertThat;

public final class RecordingBrowser implements Browser {
  private static final RecordingBrowser INSTANCE = new RecordingBrowser();

  public static RecordingBrowser getInstance() {
    return INSTANCE;
  }

  private volatile boolean connected;
  private volatile HttpUrl loadedUrl;
  private volatile boolean pageShown;

  private RecordingBrowser() {}

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

  public void assertPageShown(HttpUrl url) {
    assertThat(connected).isTrue();
    assertThat(loadedUrl).isEqualTo(url);
    assertThat(pageShown).isTrue();
  }
}
